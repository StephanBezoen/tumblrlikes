package nl.acidcats.tumblrlikes.ui.screens.photo_screen

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.github.ajalt.timberkt.Timber
import kotlinx.android.synthetic.main.fragment_photo.*
import nl.acidcats.tumblrlikes.R
import nl.acidcats.tumblrlikes.core.constants.FilterType
import nl.acidcats.tumblrlikes.di.AppComponent
import nl.acidcats.tumblrlikes.ui.screens.base.BaseFragment
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.PhotoScreenContract.Keys.REFRESH
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets.InteractiveImageView
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets.InteractiveImageView.Gesture.*
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets.photooptions.PhotoOptionsContract
import nl.acidcats.tumblrlikes.util.DeviceUtil
import nl.acidcats.tumblrlikes.util.GlideApp
import nl.acidcats.tumblrlikes.util.GlideRequest
import nl.acidcats.tumblrlikes.util.permissions.PermissionHelper
import nl.acidcats.tumblrlikes.util.permissions.PermissionListener
import javax.inject.Inject

/**
 * Created on 31/10/2018.
 */
class PhotoFragment : BaseFragment(), PhotoScreenContract.View {

    @Inject
    lateinit var presenter: PhotoScreenContract.Presenter
    @Inject
    lateinit var permissionHelper: PermissionHelper

    private val handler: Handler = Handler()
    private val uiHider: Runnable = Runnable { hideUI() }
    private val isTest: Boolean = DeviceUtil.isEmulator
    private lateinit var screenViewModel: PhotoScreenViewModel
    private val storagePermissionListener: PermissionListener = { permission, isGranted ->
        if (permission == PermissionHelper.Permission.WRITE_EXTERNAL_STORAGE && isGranted) {
            savePhoto()
        }
    }
    private val screenSize = Point()

    companion object {
        private const val HIDE_UI_DELAY_MS = 2000L

        fun newInstance(refreshLikes: Boolean): PhotoFragment {
            val fragment = PhotoFragment()

            fragment.arguments = bundleOf(REFRESH to refreshLikes)

            return fragment
        }
    }

    override fun injectFrom(appComponent: AppComponent) = appComponent.inject(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        screenViewModel = ViewModelProviders.of(activity!!).get(PhotoScreenViewModel::class.java)

        presenter.readArguments(arguments)

        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_photo, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.setView(this)
        presenter.setScreenViewModel(screenViewModel)

        photoView.setGestureListener { gesture, point ->
            onGesture(gesture, point)
        }

        activity?.windowManager?.defaultDisplay?.getRealSize(screenSize)
        photoView.screenSize = screenSize

        photoOptionsView.initViewModel(screenViewModel, this)
        photoOptionsView.setOptionSelectedListener {
            when (it) {
                PhotoOptionsContract.Option.FAVORITE -> presenter.togglePhotoFavorite()
                PhotoOptionsContract.Option.LIKE -> presenter.togglePhotoLike()
                PhotoOptionsContract.Option.HIDE -> presenter.hidePhoto()
                PhotoOptionsContract.Option.SAVE -> presenter.savePhoto()
            }
        }

        photoNavBar.filterTypeSelectedListener = { presenter.onFilterSelected(it) }
        photoNavBar.navBarListener = presenter

        permissionHelper.addPermissionListener(storagePermissionListener)

        if (isTest) photoView.alpha = .05f

        presenter.onViewCreated()
    }

    private fun onGesture(gesture: InteractiveImageView.Gesture, point: PointF?) {
        when (gesture) {
            SIDE_SWIPE -> presenter.onSwipe()
            TAP -> presenter.onTap(point!!)
            LONG_PRESS -> presenter.onLongPress()
            DOUBLE_TAP -> presenter.onDoubleTap()
        }
    }

    override fun onResume() {
        super.onResume()

        presenter.onResume()
    }

    override fun onPause() {
        super.onPause()

        presenter.onPause()

        handler.removeCallbacks(uiHider)
    }

    override fun loadPhoto(url: String, fallbackUrl: String) {
        if (context == null) return

        getGlideRequest(url)
                .error(getGlideRequest(fallbackUrl))
                .into(DrawableImageViewTarget(photoView))
    }

    private fun getGlideRequest(url: String): GlideRequest<Drawable> {
        return GlideApp.with(context!!)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
    }

    override fun resetPhotoScale() = photoView.resetScale()

    override fun scalePhotoToView() = photoView.scaleToView()

    override fun isPhotoScaled(): Boolean {
        return photoView.isScaled
    }

    override fun hidePhotoActionDialog(hideFlow: PhotoScreenContract.HideFlow) = photoOptionsView.hide(hideFlow)

    override fun showPhotoActionDialog(point: PointF) = photoOptionsView.show()

    override fun setFilter(filter: FilterType) = photoNavBar.setFilter(filter)

    override fun showUI() {
        photoView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )

        handler.removeCallbacks(uiHider)
        handler.postDelayed(uiHider, HIDE_UI_DELAY_MS)

        photoNavBar.show()
    }

    override fun hideUI() {
        photoView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LOW_PROFILE
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE
                )

        photoNavBar.hide()
    }

    override fun enableRefreshButton(enabled: Boolean) {
        photoNavBar.enableRefreshButton(enabled)
    }

    override fun checkSavePhoto() {
        if (context == null || activity == null) return

        if (permissionHelper.hasPermission(context!!, PermissionHelper.Permission.WRITE_EXTERNAL_STORAGE)) {
            savePhoto()
        } else {
            permissionHelper.requestPermission(activity!!, PermissionHelper.Permission.WRITE_EXTERNAL_STORAGE, "")
        }
    }

    private fun savePhoto() {
        val bitmap = Bitmap.createBitmap(screenSize.x, screenSize.y, Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        photoView.draw(canvas)

        presenter.saveBitmap(bitmap)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroyView() {
        permissionHelper.removePermissionListener(storagePermissionListener)

        photoOptionsView.onDestroyView()
        photoView.onDestroyView()
        photoNavBar.onDestroyView()

        presenter.onDestroyView()

        super.onDestroyView()
    }
}