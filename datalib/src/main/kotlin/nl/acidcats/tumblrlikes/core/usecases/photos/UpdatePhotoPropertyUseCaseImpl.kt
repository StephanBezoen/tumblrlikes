package nl.acidcats.tumblrlikes.core.usecases.photos

import nl.acidcats.tumblrlikes.core.models.Photo
import nl.acidcats.tumblrlikes.core.repositories.PhotoDataRepository
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created on 24/10/2018.
 */
class UpdatePhotoPropertyUseCaseImpl @Inject constructor(private val photoDataRepository: PhotoDataRepository) : UpdatePhotoPropertyUseCase {
    override fun updateLike(id: Long, isLiked: Boolean): Observable<Photo?> {
        return applyUpdate(id) {
            photoDataRepository.setPhotoLiked(id, isLiked)
        }
    }

    override fun updateFavorite(id: Long, isFavorite: Boolean): Observable<Photo?> {
        return applyUpdate(id) {
            photoDataRepository.setPhotoFavorite(id, isFavorite)

            if (isFavorite) {
                photoDataRepository.setPhotoLiked(id, true)
            }
        }
    }

    override fun setHidden(id: Long): Observable<Photo?> {
        return applyUpdate(id) {
            photoDataRepository.setPhotoLiked(id, false)
            photoDataRepository.setPhotoFavorite(id, false)

            photoDataRepository.hidePhoto(id)
        }
    }

    fun applyUpdate(id: Long, updateProperty: () -> Unit): Observable<Photo?> {
        return Observable.fromCallable { updateProperty() }
                .map { photoDataRepository.getPhotoById(id) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}