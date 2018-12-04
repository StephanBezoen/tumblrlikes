package nl.acidcats.tumblrlikes.ui.screens.photo_screen

import android.graphics.Bitmap
import android.graphics.PointF
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import com.github.ajalt.timberkt.Timber
import nl.acidcats.tumblrlikes.R
import nl.acidcats.tumblrlikes.core.constants.FilterType
import nl.acidcats.tumblrlikes.core.constants.LoadLikesMode
import nl.acidcats.tumblrlikes.core.usecases.likes.GetLikesUseCase
import nl.acidcats.tumblrlikes.core.usecases.photos.*
import nl.acidcats.tumblrlikes.core.viewmodels.PhotoViewModel
import nl.acidcats.tumblrlikes.core.viewmodels.ValidPhotoViewModel
import nl.acidcats.tumblrlikes.ui.Broadcasts
import nl.acidcats.tumblrlikes.ui.screens.base.BasePresenterImpl
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.PhotoScreenContract.RefreshType.AUTOMATIC
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.PhotoScreenContract.RefreshType.MANUAL
import java.util.*
import javax.inject.Inject

/**
 * Created on 31/10/2018.
 */
class PhotoScreenPresenter @Inject constructor() : BasePresenterImpl<PhotoScreenContract.View>(), PhotoScreenContract.Presenter {

    private val KEY_VIEW_MODEL = "key_" + "PhotoScreenPresenter" + "_viewModel"

    @Inject
    lateinit var updatePhotoPropertyUseCase: UpdatePhotoPropertyUseCase
    @Inject
    lateinit var photoViewUseCase: PhotoViewUseCase
    @Inject
    lateinit var photoFilterUseCase: PhotoFilterUseCase
    @Inject
    lateinit var getFilteredPhotoUseCase: GetFilteredPhotoUseCase
    @Inject
    lateinit var getLikesUseCase: GetLikesUseCase
    @Inject
    lateinit var saveScreenshotUseCase: SaveScreenshotUseCase

    private val loadingInterruptor: MutableList<Boolean> = ArrayList()
    private var shouldRefresh = false
    private lateinit var screenViewModel: PhotoScreenViewModel

    override fun setScreenViewModel(viewModel: PhotoScreenViewModel) {
        screenViewModel = viewModel
    }

    private fun getPhotoViewModel(): ValidPhotoViewModel? {
        return screenViewModel.getPhoto().value
    }

    override fun onViewCreated() {
        registerSubscription(
                photoFilterUseCase
                        .getSelectedFilterType()
                        .subscribe { filterType -> getView()?.setFilter(filterType) }
        )

        if (shouldRefresh) {
            shouldRefresh = false

            getView()?.clearArgument(PhotoScreenContract.Keys.REFRESH)

            refreshLikes(AUTOMATIC)
        }

        getPhotoViewModel().apply {
            if (this == null) {
                showNextPhoto()
            } else {
                showPhoto(this)
            }
        }
    }

    private fun showPhoto(viewModel: ValidPhotoViewModel) {
        getView()?.loadPhoto(viewModel.url, viewModel.fallbackUrl)

        startPhotoView()
    }

    private fun showNextPhoto() {
        getView()?.resetPhotoScale()

        endPhotoView()

        getFilteredPhotoUseCase
                .getNextFilteredPhoto()
                .filter { it is ValidPhotoViewModel }
                .map { it as ValidPhotoViewModel }
                .map {
                    screenViewModel.setPhoto(it)

                    showPhoto(it)
                }
                .subscribe({}, {
                    Timber.e { "getNextFilteredPhoto: ${it.message}" }
                })
    }

    override fun onFilterSelected(filter: FilterType) {
        registerSubscription(
                photoFilterUseCase
                        .storeFilterSelection(filter)
                        .subscribe {
                            showNextPhoto()

                            getView()?.hideUI()
                        }
        )
    }

    override fun readArguments(args: Bundle?) {
        shouldRefresh = args?.getBoolean(PhotoScreenContract.Keys.REFRESH) ?: false
    }

    override fun onPause() {
        endPhotoView()
    }

    override fun onResume() {
        getView()?.hideUI()
    }

    private fun startPhotoView() {
        getPhotoViewModel()?.apply {
            registerSubscription(photoViewUseCase.startPhotoView(photoId, SystemClock.elapsedRealtime()).subscribe())
        }
    }

    private fun endPhotoView() {
        getPhotoViewModel()?.apply {
            registerSubscription(photoViewUseCase.endPhotoView(photoId, SystemClock.elapsedRealtime()).subscribe())
        }
    }

    override fun hidePhoto() {
        getPhotoViewModel()?.apply {
            registerSubscription(
                    updatePhotoPropertyUseCase
                            .setHidden(photoId)
                            .subscribe {
                                getView()?.hidePhotoActionDialog(PhotoScreenContract.HideFlow.INSTANT)

                                showNextPhoto()
                            }
            )
        }
    }

    override fun togglePhotoLike() {
        getPhotoViewModel()?.apply {
            registerSubscription(
                    updatePhotoPropertyUseCase
                            .updateLike(photoId, !isLiked)
                            .subscribe { onPhotoPropertyUpdated(it) }
            )
        }
    }

    override fun togglePhotoFavorite() {
        getPhotoViewModel()?.apply {
            registerSubscription(
                    updatePhotoPropertyUseCase
                            .updateFavorite(photoId, !isFavorite)
                            .subscribe { onPhotoPropertyUpdated(it) }
            )
        }
    }

    private fun onPhotoPropertyUpdated(viewModel: PhotoViewModel) {
        if (viewModel is ValidPhotoViewModel) {
            screenViewModel.setPhoto(viewModel)
        }

        getView()?.hidePhotoActionDialog(PhotoScreenContract.HideFlow.ANIMATED)
    }

    override fun onSwipe() = showNextPhoto()

    override fun onTap(point: PointF) {
        getView()?.showPhotoActionDialog(point)
    }

    override fun onLongPress() {
        getView()?.showUI()
    }

    override fun onDoubleTap() {
        getView()?.let { view ->
            if (view.isPhotoScaled()) {
                view.resetPhotoScale()
            } else {
                view.scalePhotoToView()
            }
        }
    }

    override fun goSettings() {
        getView()?.sendBroadcast(Broadcasts.SETTINGS_REQUEST)
    }

    override fun refreshLikes() {
        refreshLikes(PhotoScreenContract.RefreshType.MANUAL)
    }

    private fun refreshLikes(refreshType: PhotoScreenContract.RefreshType) {
        getView()?.enableRefreshButton(false)

        registerSubscription(
                getLikesUseCase
                        .loadAllLikes(LoadLikesMode.SINCE_LAST, loadingInterruptor, Date().time)
                        .subscribe({ handleLikesLoaded(it, refreshType) }, { handleLoadPageError(it) })
        )
    }

    private fun handleLoadPageError(throwable: Throwable) {
        getView()?.enableRefreshButton(true)

        showRefreshCompleteToast(false)
    }

    private fun showRefreshCompleteToast(success: Boolean, photoCount: Int = 0) {
        val message = if (success) {
            if (photoCount == 0) {
                getView()?.getContext()?.getString(R.string.refresh_success_no_new_photos)
            } else {
                getView()?.getContext()?.getString(R.string.refresh_success, photoCount.toString())
            }
        } else {
            getView()?.getContext()?.getString(R.string.refresh_error)
        }

        getView()?.showToast(message)
    }

    private fun handleLikesLoaded(photoCount: Int, refreshType: PhotoScreenContract.RefreshType) {
        getView()?.enableRefreshButton(true)

        when (refreshType) {
            MANUAL -> showRefreshCompleteToast(true, photoCount)
            AUTOMATIC -> if (photoCount > 0) {
                showRefreshCompleteToast(true, photoCount)
            }
        }

        if (photoCount > 0) {
            getView()?.sendBroadcast(Broadcasts.CACHE_SERVICE_REQUEST)
        }
    }

    override fun savePhoto() {
        getView()?.hidePhotoActionDialog(PhotoScreenContract.HideFlow.ANIMATED)

        getView()?.checkSavePhoto()
    }

    override fun saveBitmap(bitmap: Bitmap) {
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path + "/tumblrlikes"
        val filename = Date().time.toString() + ".jpg"

        registerSubscription(
                saveScreenshotUseCase
                        .saveScreenshot(bitmap, path, filename)
                        .subscribe({ isSaved ->
                            if (isSaved) {
                                getView()?.showToast(getView()?.getContext()?.getString(R.string.photo_saved, filename))
                            } else {
                                getView()?.showToast(getView()?.getContext()?.getString(R.string.photo_save_error))
                            }
                        }, {
                            getView()?.showToast(getView()?.getContext()?.getString(R.string.photo_save_error))
                        })
        )
    }
}