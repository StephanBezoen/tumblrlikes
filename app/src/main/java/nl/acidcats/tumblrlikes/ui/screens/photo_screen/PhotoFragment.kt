package nl.acidcats.tumblrlikes.ui.screens.photo_screen

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.DrawableImageViewTarget
import kotlinx.android.synthetic.main.fragment_photo.*
import nl.acidcats.tumblrlikes.BuildConfig
import nl.acidcats.tumblrlikes.R
import nl.acidcats.tumblrlikes.core.constants.FilterType
import nl.acidcats.tumblrlikes.di.AppComponent
import nl.acidcats.tumblrlikes.ui.screens.base.BaseFragment
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.PhotoScreenContract.Keys.REFRESH
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.viewmodels.PhotoOptionsViewModel
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets.InteractiveImageView
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets.InteractiveImageView.Gesture.*
import nl.acidcats.tumblrlikes.util.GlideApp
import nl.acidcats.tumblrlikes.util.GlideRequest
import javax.inject.Inject

/**
 * Created on 31/10/2018.
 */
class PhotoFragment : BaseFragment(), PhotoScreenContract.View {

    @Inject
    lateinit var presenter: PhotoScreenContract.Presenter

    private val handler: Handler = Handler()
    private val uiHider: Runnable = Runnable { hideUI() }
    private val isTest: Boolean = BuildConfig.DEMO

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

        presenter.restoreState(savedInstanceState, arguments)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_photo, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.setView(this)
        presenter.onViewCreated()

        photoView.setGestureListener { onGesture(it) }

        photoActionDialog.setPhotoActionListener(presenter)

        photoNavBar.filterTypeSelectedListener = { presenter.onFilterSelected(it) }
        photoNavBar.navBarListener = presenter

        if (isTest) photoView.alpha = .05f
    }

    private fun onGesture(gesture: InteractiveImageView.Gesture?) {
        when (gesture) {
            SIDE_SWIPE -> presenter.onSwipe()
            TAP -> presenter.onTap()
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

    override fun hidePhotoActionDialog(hideFlow: PhotoScreenContract.HideFlow) = photoActionDialog.hide(hideFlow)

    override fun showPhotoActionDialog(viewModel: PhotoOptionsViewModel) = photoActionDialog.show(viewModel)

    override fun setFilter(filter: FilterType) = photoNavBar.setFilter(filter)

    override fun setPhotoOptionsViewModel(viewModel: PhotoOptionsViewModel) = photoActionDialog.updateViewModel(viewModel)

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

    override fun setPhotoVisible(visible: Boolean) {
        photoView.visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }

    override fun enableRefreshButton(enabled: Boolean) {
        photoNavBar.enableRefreshButton(enabled)
    }

    override fun showRefreshCompleteToast(success: Boolean, photoCount: Int) {
        val message = if (success) {
            if (photoCount == 0) {
                getString(R.string.refresh_success_no_new_photos)
            } else {
                getString(R.string.refresh_success, photoCount.toString())
            }
        } else {
            getString(R.string.refresh_error)
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        photoActionDialog.onDestroyView()
        photoView.onDestroyView()
        photoNavBar.onDestroyView()

        presenter.onDestroyView()

        super.onDestroyView()
    }
}