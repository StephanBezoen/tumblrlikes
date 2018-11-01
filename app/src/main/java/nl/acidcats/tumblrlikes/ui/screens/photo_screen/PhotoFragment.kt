package nl.acidcats.tumblrlikes.ui.screens.photo_screen

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.target.Target
import nl.acidcats.tumblrlikes.R
import nl.acidcats.tumblrlikes.di.AppComponent
import nl.acidcats.tumblrlikes.ui.screens.base.BaseFragment
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.constants.Filter
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.viewmodels.PhotoOptionsViewModel
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets.InteractiveImageView
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets.InteractiveImageView.Gesture.*
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets.PhotoActionDialog
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets.PhotoNavBar
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets.filterdropdown.FilterOptionSelectionListener
import nl.acidcats.tumblrlikes.util.GlideApp
import javax.inject.Inject

/**
 * Created on 31/10/2018.
 */
class PhotoFragment : BaseFragment(), PhotoScreenContract.View {

    private val HIDE_UI_DELAY_MS = 2000L

    @Inject
    lateinit var presenter: PhotoScreenContract.Presenter

    @BindView(R.id.photo)
    lateinit var photoView: InteractiveImageView
    @BindView(R.id.photo_action_dialog)
    lateinit var photoActionDialog: PhotoActionDialog;
    @BindView(R.id.photo_nav_bar)
    lateinit var photoNavBar: PhotoNavBar

    private val handler: Handler = Handler()
    private val uiHider: Runnable = Runnable { hideUI() }
    private val isTest: Boolean = true

    companion object {
        fun newInstance(): PhotoFragment = PhotoFragment()
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

        photoNavBar.filterOptionSelectionListener = object : FilterOptionSelectionListener {
            override fun onOptionSelected(filterType: Filter) {
                setFilter(filterType)
            }
        }

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

    override fun loadPhoto(url: String?, notifyOnError: Boolean) {
        if (context == null) return

        var listener: RequestListener<Drawable>? = null
        if (notifyOnError) {
            listener = object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    if (activity != null) {
                        activity!!.runOnUiThread { presenter.onImageLoadFailed() }
                    }
                    return true
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean = false
            }
        }

        GlideApp.with(context!!)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .listener(listener)
                .into(DrawableImageViewTarget(photoView))
    }

    override fun resetPhotoScale() = photoView.resetScale()

    override fun hidePhotoActionDialog(hideFlow: PhotoScreenContract.HideFlow) = photoActionDialog.hide(hideFlow)

    override fun showPhotoActionDialog(viewModel: PhotoOptionsViewModel) = photoActionDialog.show(viewModel)

    override fun setFilter(filter: Filter) = photoNavBar.setFilter(filter)

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

    override fun onDestroyView() {
        photoActionDialog.onDestroyView()
        photoView.onDestroyView()
        photoNavBar.onDestroyView()

        presenter.onDestroyView()

        super.onDestroyView()
    }
}