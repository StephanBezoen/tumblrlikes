package nl.acidcats.tumblrlikes.core.usecases.photos;

import javax.inject.Inject;

import nl.acidcats.tumblrlikes.core.repositories.PhotoDataRepository;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created on 01/10/2018.
 */
public class PhotoCacheUseCaseImpl implements PhotoCacheUseCase {

    private PhotoDataRepository _photoDataRepository;

    @Inject
    public PhotoCacheUseCaseImpl(PhotoDataRepository photoDataRepository) {
        _photoDataRepository = photoDataRepository;
    }

    @Override
    public Observable<Void> removeCachedHiddenPhotos() {
        return _photoDataRepository
                .removeCachedHiddenPhotos()
                .observeOn(AndroidSchedulers.mainThread());
    }
}
