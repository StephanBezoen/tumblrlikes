package nl.acidcats.tumblrlikes.ui.screens.photo_screen

import android.os.Bundle
import android.os.SystemClock
import com.github.ajalt.timberkt.Timber
import nl.acidcats.tumblrlikes.core.constants.FilterType
import nl.acidcats.tumblrlikes.core.models.Photo
import nl.acidcats.tumblrlikes.core.usecases.photos.GetFilteredPhotoUseCase
import nl.acidcats.tumblrlikes.core.usecases.photos.PhotoFilterUseCase
import nl.acidcats.tumblrlikes.core.usecases.photos.PhotoViewUseCase
import nl.acidcats.tumblrlikes.core.usecases.photos.UpdatePhotoPropertyUseCase
import nl.acidcats.tumblrlikes.ui.screens.base.BasePresenterImpl
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.viewmodels.PhotoOptionsViewModel
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.viewmodels.PhotoViewViewModel
import rx.Observable
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

    private var viewModel: PhotoViewViewModel? = null

    override fun onViewCreated() {
        registerSubscription(
                photoFilterUseCase
                        .getSelectedFilterType()
                        .subscribe { filterType -> getView()?.setFilter(filterType) }
        )

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
                    getView()?.loadPhoto(it!!.url, true)

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

    override fun onImageLoadFailed() {
        if (PhotoViewViewModel.isValid(viewModel)) {
            getView()?.loadPhoto(viewModel!!.fallbackUrl, false)
        }
    }

    override fun onFilterSelected(filter: FilterType) {
        registerSubscription(
                photoFilterUseCase
                        .storeFilterSelection(filter)
                        .subscribe { showNextPhoto() }

        )
    }

    override fun saveState(outState: Bundle) {
        if (PhotoViewViewModel.isValid(viewModel)) {
            outState.putParcelable(KEY_VIEW_MODEL, viewModel)
        }
    }

    override fun restoreState(savedInstanceState: Bundle?, args: Bundle?) {
        viewModel = savedInstanceState?.getParcelable(KEY_VIEW_MODEL)
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

    override fun onTap() {
        getView()?.showUI()
    }

    override fun onLongPress() {
        if (PhotoViewViewModel.isValid(viewModel)) {
            getView()?.showPhotoActionDialog(createPhotoOptionsViewModel(viewModel!!))
        }
    }

    override fun onDoubleTap() {
        getView()?.resetPhotoScale()
    }
}