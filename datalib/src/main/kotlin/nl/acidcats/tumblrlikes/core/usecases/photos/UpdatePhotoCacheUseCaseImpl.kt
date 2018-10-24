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
class UpdatePhotoCacheUseCaseImpl @Inject constructor(private val photoDataRepository: PhotoDataRepository) : UpdatePhotoCacheUseCase {

    override fun removeCachedHiddenPhotos(): Observable<Boolean> {
        return photoDataRepository
                .removeCachedHiddenPhotos()
                .observeOn(AndroidSchedulers.mainThread())
    }

    // TODO use filter & map from collections to do filtering & mapping
    override fun checkCachedPhotos(): Observable<Int> {
        return Observable
                .fromCallable { photoDataRepository.getCachedPhotos() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapIterable { it }
                .filter(photoDataRepository::isPhotoCacheMissing)
                .map(Photo::id)
                .toList()
                .map(photoDataRepository::setPhotosUncached)
                .map(List<Long>::size)
    }
}