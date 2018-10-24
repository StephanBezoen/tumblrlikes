package nl.acidcats.tumblrlikes.core.usecases.photos

import javax.inject.Inject

import nl.acidcats.tumblrlikes.core.repositories.PhotoDataRepository
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created on 17/10/2018.
 */
class PhotoViewUseCaseImpl @Inject constructor(private val _photoDataRepository: PhotoDataRepository) : PhotoViewUseCase {

    override fun startPhotoView(id: Long, currentTime: Long): Observable<Boolean> {
        _photoDataRepository.setPhotoViewStartTime(id, currentTime)

        return Observable.just(true)
    }

    override fun endPhotoView(id: Long, currentTime: Long): Observable<Boolean> {
        return Observable
                .fromCallable {
                    _photoDataRepository.updatePhotoViewTime(id, currentTime)
                    true
                }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
    }
}
