package nl.acidcats.tumblrlikes.core.usecases.photos

import nl.acidcats.tumblrlikes.core.models.Photo
import nl.acidcats.tumblrlikes.core.repositories.PhotoDataRepository
import nl.acidcats.tumblrlikes.core.viewmodels.InvalidPhoto
import nl.acidcats.tumblrlikes.core.viewmodels.PhotoViewModel
import nl.acidcats.tumblrlikes.core.viewmodels.ValidPhotoViewModel
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created on 24/10/2018.
 */
class GetFilteredPhotoUseCaseImpl @Inject constructor(private val photoDataRepository: PhotoDataRepository) : GetFilteredPhotoUseCase {
    override fun getNextFilteredPhoto(): Observable<PhotoViewModel> {
        return Observable
                .fromCallable { photoDataRepository.getNextPhoto() }
                .map { PhotoViewModel.from(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}
