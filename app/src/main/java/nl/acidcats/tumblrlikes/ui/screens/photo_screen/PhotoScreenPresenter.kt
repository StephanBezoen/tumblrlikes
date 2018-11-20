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
import nl.acidcats.tumblrlikes.core.models.Photo
import nl.acidcats.tumblrlikes.core.usecases.likes.GetLikesUseCase
import nl.acidcats.tumblrlikes.core.usecases.photos.GetFilteredPhotoUseCase
import nl.acidcats.tumblrlikes.core.usecases.photos.PhotoFilterUseCase
import nl.acidcats.tumblrlikes.core.usecases.photos.PhotoViewUseCase
import nl.acidcats.tumblrlikes.core.usecases.photos.UpdatePhotoPropertyUseCase
import nl.acidcats.tumblrlikes.ui.Broadcasts
import nl.acidcats.tumblrlikes.ui.screens.base.BasePresenterImpl
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.PhotoScreenContract.RefreshType.*
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.viewmodels.PhotoOptionsViewModel
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.viewmodels.PhotoViewViewModel
import rx.Observable
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
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

    private var viewModel: PhotoViewViewModel? = null
    private val loadingInterruptor: MutableList<Boolean> = ArrayList()
    private var shouldRefresh = false

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

        showNextPhoto()
    }

    private fun showNextPhoto() {
        getView()?.resetPhotoScale()

        if (PhotoViewViewModel.isValid(viewModel)) {
            endPhotoView()
        }

        // TODO("Filtered photo use case should return PhotoViewViewModel as result")
        getFilteredPhotoUseCase
                .getNextFilteredPhoto()
                .map { createViewModel(it) }
                .filter { PhotoViewViewModel.isValid(it) }
                .flatMap {
                    getView()?.loadPhoto(it!!.url!!, it.fallbackUrl!!)

                    Observable.just(it)
                }
                .flatMap { photoViewUseCase.startPhotoView(it!!.photoId, SystemClock.elapsedRealtime()) }
                .subscribe({}, {
                    Timber.e { "getNextFilteredPhoto: ${it.message}" }
                })
    }

    private fun createViewModel(photo: Photo?): PhotoViewViewModel? {
        if (photo == null) return null

        var url = if (photo.isCached) photo.filePath else photo.url
        if (url != null && !url.startsWith("http")) url = "file:$url"

        viewModel = PhotoViewViewModel(photo.id, url, photo.url, photo.isFavorite, photo.isLiked, photo.viewCount)

        return viewModel
    }

    private fun startPhotoView() {
        if (PhotoViewViewModel.isValid(viewModel)) {
            registerSubscription(photoViewUseCase.startPhotoView(viewModel!!.photoId, SystemClock.elapsedRealtime()).subscribe())
        }
    }

    private fun endPhotoView() {
        if (PhotoViewViewModel.isValid(viewModel)) {
            registerSubscription(photoViewUseCase.endPhotoView(viewModel!!.photoId, SystemClock.elapsedRealtime()).subscribe())
        }
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

    override fun saveState(outState: Bundle) {
        if (PhotoViewViewModel.isValid(viewModel)) {
            outState.putParcelable(KEY_VIEW_MODEL, viewModel)
        }
    }

    override fun restoreState(savedInstanceState: Bundle?, args: Bundle?) {
        viewModel = savedInstanceState?.getParcelable(KEY_VIEW_MODEL)

        shouldRefresh = args?.getBoolean(PhotoScreenContract.Keys.REFRESH) ?: false
    }

    override fun onPause() {
        endPhotoView()

        getView()?.setPhotoVisible(false)
    }

    override fun onResume() {
        getView()?.setPhotoVisible(true)

        startPhotoView()

        getView()?.hideUI()
    }

    override fun onHidePhoto(id: Long) {
        registerSubscription(
                updatePhotoPropertyUseCase
                        .setHidden(id)
                        .subscribe {
                            getView()?.hidePhotoActionDialog(PhotoScreenContract.HideFlow.INSTANT)

                            showNextPhoto()
                        }
        )
    }

    override fun onUpdatePhotoLike(id: Long, isLiked: Boolean) {
        registerSubscription(
                updatePhotoPropertyUseCase
                        .updateLike(id, isLiked)
                        .subscribe { onPhotoPropertyUpdated(it) }
        )
    }

    override fun onUpdatePhotoFavorite(id: Long, isFavorite: Boolean) {
        registerSubscription(
                updatePhotoPropertyUseCase
                        .updateFavorite(id, isFavorite)
                        .subscribe { onPhotoPropertyUpdated(it) }
        )
    }

    private fun onPhotoPropertyUpdated(photo: Photo?) {
        createViewModel(photo)

        if (PhotoViewViewModel.isValid(viewModel)) {
            getView()?.setPhotoOptionsViewModel(createPhotoOptionsViewModel(viewModel!!))
        }

        getView()?.hidePhotoActionDialog(PhotoScreenContract.HideFlow.ANIMATED)
    }

    private fun createPhotoOptionsViewModel(viewModel: PhotoViewViewModel): PhotoOptionsViewModel {
        return PhotoOptionsViewModel(viewModel.photoId, viewModel.isFavorite, viewModel.isLiked, viewModel.viewCount)
    }

    override fun onSwipe() = showNextPhoto()

    override fun onTap(point:PointF) {
        if (PhotoViewViewModel.isValid(viewModel)) {
            getView()?.showPhotoActionDialog(createPhotoOptionsViewModel(viewModel!!), point)
        }
    }

    override fun onLongPress() {
        getView()?.showUI()
    }

    override fun onDoubleTap() {
        getView()?.let {view ->
            if (view.isPhotoScaled()) {
                view.resetPhotoScale()
            } else {
                view.scalePhotoToView()
            }
        }
    }

    override fun onSettingsRequested() {
        getView()?.sendBroadcast(Broadcasts.SETTINGS_REQUEST)
    }

    override fun onRefreshRequested() {
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

    override fun onSavePhoto() {
        getView()?.hidePhotoActionDialog(PhotoScreenContract.HideFlow.ANIMATED)

        getView()?.checkSavePhoto()
    }

    override fun saveBitmap(bitmap: Bitmap) {
        val filename = Date().time.toString() + ".jpg"

        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path + "/tumblrlikes"
        val dirFile = File(directory)
        if (!dirFile.exists()) {
            dirFile.mkdir()
        }

        val outputStream = BufferedOutputStream(FileOutputStream(File(dirFile.path, filename)))
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)

            getView()?.showToast(getView()?.getContext()?.getString(R.string.photo_saved, filename))
        } catch (e: Exception) {
            Timber.e { "savePhoto: " + e.message }

            getView()?.showToast(getView()?.getContext()?.getString(R.string.photo_save_error))
        } finally {
            outputStream.close()
        }
    }
}