package nl.acidcats.tumblrlikes.core.usecases.photos;

import javax.inject.Inject;

import nl.acidcats.tumblrlikes.core.repositories.PhotoDataRepository;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created on 17/10/2018.
 */
public class PhotoViewUseCaseImpl implements PhotoViewUseCase {

    private PhotoDataRepository _photoDataRepository;

    @Inject
    public PhotoViewUseCaseImpl(PhotoDataRepository photoDataRepository) {
        _photoDataRepository = photoDataRepository;
    }

    @Override
    public Observable<Boolean> startPhotoView(long id, long currentTime) {
        _photoDataRepository.setPhotoViewStartTime(id, currentTime);

        return Observable.just(true);
    }

    @Override
    public Observable<Boolean> endPhotoView(long id, long currentTime) {
        return Observable
                .fromCallable(() -> {
                    _photoDataRepository.updatePhotoViewTime(id, currentTime);
                    return true;
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
