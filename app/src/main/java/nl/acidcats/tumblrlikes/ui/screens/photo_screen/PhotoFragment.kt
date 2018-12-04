package nl.acidcats.tumblrlikes.ui.screens.photo_screen

import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_photo.*
import nl.acidcats.tumblrlikes.R
import nl.acidcats.tumblrlikes.ui.Broadcasts
import nl.acidcats.tumblrlikes.ui.screens.base.BaseFragment
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.PhotoScreenContract.Keys.REFRESH
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets.InteractiveImageView.Gesture.*
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets.PhotoNavBar
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets.photooptions.PhotoOptionsContract
import nl.acidcats.tumblrlikes.util.DeviceUtil
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
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val handler: Handler = Handler()
    private val uiHider: Runnable = Runnable { hideUI() }
    private val isTest: Boolean = DeviceUtil.isEmulator
    private lateinit var screenViewModel: PhotoScreenViewModel
    private val screenSize = Point()
    private val storagePermissionListener: PermissionListener = { permission, isGranted ->
        if (permission == PermissionHelper.Permission.WRITE_EXTERNAL_STORAGE && isGranted) {
            checkSavePhoto()
        }
    }

    companion object {
        private const val HIDE_UI_DELAY_MS = 2000L

        fun newInstance(refreshLikes: Boolean): PhotoFragment {
            val fragment = PhotoFragment()

            fragment.arguments = bundleOf(REFRESH to refreshLikes)

            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_photo, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        screenViewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(PhotoScreenViewModel::class.java)

        presenter.setView(this)
        presenter.readArguments(arguments)

        activity?.windowManager?.defaultDisplay?.getRealSize(screenSize)
        photoView.screenSize = screenSize
        photoView.setGestureListener {
            when (it) {
                SIDE_SWIPE -> screenViewModel.getNextPhoto()
                TAP -> photoOptionsView.show()
                LONG_PRESS -> showUI()
                DOUBLE_TAP -> photoView.toggleScaling()
            }
        }

        photoOptionsView.initViewModel(screenViewModel, this)
        photoOptionsView.setOptionSelectedListener {
            photoOptionsView.hide(PhotoOptionsContract.HideFlow.ANIMATED)

            when (it) {
                PhotoOptionsContract.Option.FAVORITE -> screenViewModel.togglePhotoFavorite()
                PhotoOptionsContract.Option.LIKE -> screenViewModel.togglePhotoLike()
                PhotoOptionsContract.Option.HIDE -> screenViewModel.hidePhoto()
                PhotoOptionsContract.Option.SAVE -> checkSavePhoto()
            }
        }

        photoNavBar.filterTypeSelectedListener = { screenViewModel.storeFilterSelection(it) }
        photoNavBar.navBarListener = object : PhotoNavBar.NavBarListener {
            override fun onRefreshRequested() {
                presenter.refreshLikes()
            }

            override fun onSettingsRequested() {
                sendBroadcast(Broadcasts.SETTINGS_REQUEST)
            }
        }

        permissionHelper.addPermissionListener(storagePermissionListener)

        if (isTest) photoView.alpha = .1f

        presenter.onViewCreated()

        screenViewModel.getPhoto().observe(this, Observer { photoView.loadPhoto(it.url, it.fallbackUrl) })

        screenViewModel.getFilterType().observe(this, Observer { photoNavBar.setFilter(it) })
    }

    override fun onResume() {
        super.onResume()

        hideUI()

        screenViewModel.startPhotoView()
    }

    override fun onPause() {
        super.onPause()

        screenViewModel.endPhotoView()

        handler.removeCallbacks(uiHider)
    }

    private fun showUI() {
        handler.removeCallbacks(uiHider)
        handler.postDelayed(uiHider, HIDE_UI_DELAY_MS)

        photoNavBar.show()
    }

    private fun hideUI() {
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

    private fun checkSavePhoto() {
        if (context == null || activity == null) return

        if (permissionHelper.hasPermission(context!!, PermissionHelper.Permission.WRITE_EXTERNAL_STORAGE)) {
            presenter.saveBitmap(photoView.getBitmapSnapshot())
        } else {
            permissionHelper.requestPermission(activity!!, PermissionHelper.Permission.WRITE_EXTERNAL_STORAGE, "")
        }
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