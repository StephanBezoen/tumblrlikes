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
import com.github.ajalt.timberkt.Timber
import kotlinx.android.synthetic.main.fragment_photo.*
import nl.acidcats.tumblrlikes.R
import nl.acidcats.tumblrlikes.ui.Broadcasts
import nl.acidcats.tumblrlikes.ui.screens.base.BaseFragment
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets.InteractiveImageView.Gesture.*
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets.PhotoNavBar
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets.photooptions.PhotoOptionsContract
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets.photooptions.PhotoOptionsContract.Option.*
import nl.acidcats.tumblrlikes.ui.viewmodels.LikesViewModel
import nl.acidcats.tumblrlikes.ui.viewmodels.LikesViewModel.LoadingState
import nl.acidcats.tumblrlikes.ui.viewmodels.PhotoScreenViewModel
import nl.acidcats.tumblrlikes.ui.viewmodels.PhotoScreenViewModel.SaveState
import nl.acidcats.tumblrlikes.util.DeviceUtil
import nl.acidcats.tumblrlikes.util.permissions.PermissionHelper
import nl.acidcats.tumblrlikes.util.permissions.PermissionListener
import javax.inject.Inject

/**
 * Created on 31/10/2018.
 */
class PhotoFragment : BaseFragment() {

    @Inject
    lateinit var permissionHelper: PermissionHelper
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val handler: Handler = Handler()
    private val uiHider: Runnable = Runnable { hideUI() }
    private val isTest: Boolean = DeviceUtil.isEmulator
    private lateinit var photoViewModel: PhotoScreenViewModel
    private lateinit var likesViewModel: LikesViewModel
    private val storagePermissionListener: PermissionListener = { permission, isGranted ->
        if (permission == PermissionHelper.Permission.WRITE_EXTERNAL_STORAGE && isGranted) {
            savePhoto()
        }
    }

    companion object {
        private const val HIDE_UI_DELAY_MS = 2000L
        const val KEY_REFRESH = "Refresh"

        fun newInstance(refreshLikes: Boolean): PhotoFragment {
            val fragment = PhotoFragment()

            fragment.arguments = bundleOf(KEY_REFRESH to refreshLikes)

            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_photo, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        photoViewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(PhotoScreenViewModel::class.java)
        photoViewModel.getPhoto().observe(this, Observer { photoView.loadPhoto(it.url, it.fallbackUrl) })
        photoViewModel.getFilterType().observe(this, Observer { photoNavBar.setFilter(it) })
        photoViewModel.getSaveState().observe(this, Observer { onPhotoSaveStateChanged(it) })

        likesViewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(LikesViewModel::class.java)
        likesViewModel.getLoadingState().observe(this, Observer { onLikesLoadingStateChanged(it) })

        initPhotoView()

        initPhotoOptionsView()

        initNavBar()

        permissionHelper.addPermissionListener(storagePermissionListener)

        if (isTest) photoView.alpha = .1f

        checkRefreshLikes()
    }

    private fun initNavBar() {
        photoNavBar.filterTypeSelectedListener = { photoViewModel.storeFilterSelection(it) }
        photoNavBar.navBarListener = object : PhotoNavBar.NavBarListener {
            override fun onRefreshRequested() {
                likesViewModel.refreshLikes(LikesViewModel.RefreshType.MANUAL)
            }

            override fun onSettingsRequested() {
                sendBroadcast(Broadcasts.SETTINGS_REQUEST)
            }
        }
    }

    private fun initPhotoOptionsView() {
        photoOptionsView.initViewModel(photoViewModel, this)

        photoOptionsView.setOptionSelectedListener {
            photoOptionsView.hide(PhotoOptionsContract.HideFlow.ANIMATED)

            when (it) {
                FAVORITE -> photoViewModel.togglePhotoFavorite()
                LIKE -> photoViewModel.togglePhotoLike()
                HIDE -> photoViewModel.hidePhoto()
                SAVE -> checkSavePhoto()
            }
        }
    }

    private fun initPhotoView() {
        val screenSize = Point()
        activity?.windowManager?.defaultDisplay?.getRealSize(screenSize)
        photoView.setScreenSize(screenSize)

        photoView.setGestureListener {
            when (it) {
                SIDE_SWIPE -> photoViewModel.getNextPhoto()
                TAP -> photoOptionsView.show()
                LONG_PRESS -> showUI()
                DOUBLE_TAP -> photoView.toggleScaling()
            }
        }
    }

    private fun checkRefreshLikes() {
        val shouldRefresh = arguments?.getBoolean(KEY_REFRESH) ?: false
        if (shouldRefresh) {
            arguments?.putBoolean(KEY_REFRESH, false)

            likesViewModel.refreshLikes(LikesViewModel.RefreshType.AUTOMATIC)
        }
    }

    private fun onLikesLoadingStateChanged(loadingState: LikesViewModel.LoadingState) {
        Timber.d { "onLikesLoadingStateChanged: $loadingState" }

        when (loadingState) {
            is LoadingState.IDLE -> enableRefreshButton(true)
            is LoadingState.LOADING -> enableRefreshButton(false)
            is LoadingState.SUCCESS -> onLikesLoadSuccess()
            is LoadingState.ERROR -> onLikesLoadError()
        }
    }

    private fun enableRefreshButton(enabled: Boolean) {
        photoNavBar.enableRefreshButton(enabled)
    }

    private fun onLikesLoadError() {
        showRefreshCompleteToast(false)

        likesViewModel.resetLoadingState()
    }

    private fun onLikesLoadSuccess() {
        showRefreshCompleteToast(true, likesViewModel.getLoadedLikesCount().value)

        likesViewModel.resetLoadingState()
    }

    private fun showRefreshCompleteToast(success: Boolean, photoCount: Int? = 0) {
        val message = if (success) {
            if (photoCount == 0) {
                getString(R.string.refresh_success_no_new_photos)
            } else {
                getString(R.string.refresh_success, photoCount.toString())
            }
        } else {
            getString(R.string.refresh_error)
        }

        showToast(message)
    }

    override fun onResume() {
        super.onResume()

        hideUI()

        photoViewModel.startPhotoView()
    }

    override fun onPause() {
        super.onPause()

        handler.removeCallbacks(uiHider)

        photoViewModel.endPhotoView()
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

    private fun checkSavePhoto() {
        if (context == null || activity == null) return

        if (permissionHelper.hasPermission(context!!, PermissionHelper.Permission.WRITE_EXTERNAL_STORAGE)) {
            savePhoto()
        } else {
            permissionHelper.requestPermission(activity!!, PermissionHelper.Permission.WRITE_EXTERNAL_STORAGE, "")
        }
    }

    private fun savePhoto() {
        photoViewModel.saveBitmap(photoView.getBitmapSnapshot())
    }

    private fun onPhotoSaveStateChanged(saveState: PhotoScreenViewModel.SaveState) {
        Timber.d { "onPhotoSaveStateChanged: $saveState" }

        when (saveState) {
            is SaveState.SUCCESS -> onPhotoSaved(saveState)
            is SaveState.ERROR -> onPhotoSaveError()
        }
    }

    private fun onPhotoSaveError() {
        showToast(getString(R.string.photo_save_error))

        photoViewModel.resetSaveState()
    }

    private fun onPhotoSaved(saveState: PhotoScreenViewModel.SaveState.SUCCESS) {
        showToast(getString(R.string.photo_saved, saveState.filename))

        photoViewModel.resetSaveState()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroyView() {
        permissionHelper.removePermissionListener(storagePermissionListener)

        photoOptionsView.onDestroyView()
        photoView.onDestroyView()
        photoNavBar.onDestroyView()

        super.onDestroyView()
    }
}