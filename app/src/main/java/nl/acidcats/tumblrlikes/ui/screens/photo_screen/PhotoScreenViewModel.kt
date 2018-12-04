package nl.acidcats.tumblrlikes.ui.screens.photo_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import nl.acidcats.tumblrlikes.core.viewmodels.ValidPhotoViewModel

/**
 * Created on 30/10/2018.
 */
class PhotoScreenViewModel : ViewModel() {
    private val validPhoto: MutableLiveData<ValidPhotoViewModel> = MutableLiveData()

    fun getPhoto(): LiveData<ValidPhotoViewModel> {
        return validPhoto
    }

    fun setPhoto(photoViewModel: ValidPhotoViewModel) {
        this.validPhoto.value = photoViewModel
    }
}