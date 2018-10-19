package nl.acidcats.tumblrlikes.core.usecases.photos;

import javax.inject.Inject;

import nl.acidcats.tumblrlikes.core.models.Photo;
import nl.acidcats.tumblrlikes.core.repositories.PhotoDataRepository;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created on 19/10/2018.
 */
public class GetFilteredPhotoUseCaseImpl implements GetFilteredPhotoUseCase {

    private PhotoDataRepository _photoDataRepository;

    @Inject
    public GetFilteredPhotoUseCaseImpl(PhotoDataRepository photoDataRepository) {
        _photoDataRepository = photoDataRepository;
    }

    @Override
    public Observable<Photo> getNextFilteredPhoto() {
        return Observable
                .fromCallable(() -> _photoDataRepository.getNextPhoto())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
