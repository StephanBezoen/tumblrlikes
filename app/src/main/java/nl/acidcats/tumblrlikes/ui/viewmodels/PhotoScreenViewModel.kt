package nl.acidcats.tumblrlikes.ui.viewmodels

import android.graphics.Bitmap
import android.os.Environment
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.ajalt.timberkt.Timber
import nl.acidcats.tumblrlikes.core.constants.FilterType
import nl.acidcats.tumblrlikes.core.usecases.photos.*
import nl.acidcats.tumblrlikes.core.viewmodels.ValidPhotoViewModel
import nl.acidcats.tumblrlikes.ui.screens.base.BaseViewModel
import java.util.*
import javax.inject.Inject

/**
 * Created on 30/10/2018.
 */
class PhotoScreenViewModel @Inject constructor(
        private val getFilteredPhotoUseCase: GetFilteredPhotoUseCase,
        private val photoViewUseCase: PhotoViewUseCase,
        private val updatePhotoPropertyUseCase: UpdatePhotoPropertyUseCase,
        private val photoFilterUseCase: PhotoFilterUseCase,
        private val saveScreenshotUseCase: SaveScreenshotUseCase
) : BaseViewModel() {

    sealed class SaveState {
        object IDLE : SaveState()
        object SAVING : SaveState()
        class SUCCESS(val filename: String) : SaveState()
        object ERROR : SaveState()
    }

    private lateinit var validPhoto: MutableLiveData<ValidPhotoViewModel>
    private lateinit var filterType: MutableLiveData<FilterType>
    private var saveState = MutableLiveData<SaveState>()

    init {
        saveState.value = SaveState.IDLE
    }

    fun getPhoto(): LiveData<ValidPhotoViewModel> {
        if (!::validPhoto.isInitialized) {
            validPhoto = MutableLiveData()

            getNextPhoto()
        }

        return validPhoto
    }

    fun getNextPhoto() {
        endPhotoView()

        registerSubscription(
                getFilteredPhotoUseCase
                        .getNextFilteredPhoto()
                        .filter { it is ValidPhotoViewModel }
                        .map { it as ValidPhotoViewModel }
                        .subscribe({
                            validPhoto.value = it

                            startPhotoView()
                        }, {
                            Timber.e { "getNextFilteredPhoto: ${it.message}" }
                        })
        )
    }

    fun startPhotoView() {
        validPhoto.value?.apply {
            registerSubscription(photoViewUseCase.startPhotoView(id = photoId, currentTime = SystemClock.elapsedRealtime()).subscribe())
        }
    }

    fun endPhotoView() {
        validPhoto.value?.apply {
            registerSubscription(photoViewUseCase.endPhotoView(id = photoId, currentTime = SystemClock.elapsedRealtime()).subscribe())
        }
    }

    fun hidePhoto() {
        validPhoto.value?.apply {
            registerSubscription(
                    updatePhotoPropertyUseCase
                            .setHidden(photoId)
                            .subscribe { getNextPhoto() }
            )
        }
    }

    fun togglePhotoLike() {
        validPhoto.value?.apply {
            registerSubscription(
                    updatePhotoPropertyUseCase
                            .updateLike(photoId, !isLiked)
                            .filter { it is ValidPhotoViewModel }
                            .map { it as ValidPhotoViewModel }
                            .subscribe { validPhoto.value = it }
            )
        }
    }

    fun togglePhotoFavorite() {
        validPhoto.value?.apply {
            registerSubscription(
                    updatePhotoPropertyUseCase
                            .updateFavorite(photoId, !isFavorite)
                            .filter { it is ValidPhotoViewModel }
                            .map { it as ValidPhotoViewModel }
                            .subscribe { validPhoto.value = it }
            )
        }
    }

    fun getFilterType(): LiveData<FilterType> {
        if (!::filterType.isInitialized) {
            filterType = MutableLiveData()

            loadFilterType()
        }
        return filterType
    }

    private fun loadFilterType() {
        registerSubscription(
                photoFilterUseCase
                        .getSelectedFilterType()
                        .subscribe { filterType.value = it }
        )
    }

    fun storeFilterSelection(filter: FilterType) {
        registerSubscription(
                photoFilterUseCase
                        .storeFilterSelection(filter)
                        .subscribe {
                            filterType.value = filter

                            getNextPhoto()
                        }
        )
    }

    fun getSaveState(): LiveData<SaveState> = saveState

    fun resetSaveState() {
        saveState.value = SaveState.IDLE
    }

    fun saveBitmap(bitmap: Bitmap) {
        saveState.value = SaveState.SAVING

        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path + "/tumblrlikes"
        val filename = Date().time.toString() + ".jpg"

        registerSubscription(
                saveScreenshotUseCase
                        .saveScreenshot(bitmap, path, filename)
                        .subscribe({ isSaved ->
                            if (isSaved) {
                                saveState.value = SaveState.SUCCESS(filename)
                            } else {
                                saveState.value = SaveState.ERROR
                            }
                        }, {
                            saveState.value = SaveState.ERROR
                        })
        )
    }
}
