package nl.acidcats.tumblrlikes.core.usecases.photos;

import java.util.List;

import javax.inject.Inject;

import nl.acidcats.tumblrlikes.core.models.Photo;
import nl.acidcats.tumblrlikes.core.repositories.PhotoDataRepository;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created on 01/10/2018.
 */
public class UpdatePhotoCacheUseCaseImpl implements UpdatePhotoCacheUseCase {

    private PhotoDataRepository _photoDataRepository;

    @Inject
    UpdatePhotoCacheUseCaseImpl(PhotoDataRepository photoDataRepository) {
        _photoDataRepository = photoDataRepository;
    }

    @Override
    public Observable<Boolean> removeCachedHiddenPhotos() {
        return _photoDataRepository
                .removeCachedHiddenPhotos()
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Integer> checkCachedPhotos() {
        return Observable
                .fromCallable(() -> _photoDataRepository.getCachedPhotos())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapIterable(photoEntities -> photoEntities)
                .filter(photo -> _photoDataRepository.isPhotoCacheMissing(photo))
                .map(Photo::getId)
                .toList()
                .map(ids -> _photoDataRepository.setPhotosUncached(ids))
                .map(List::size);
    }
}
