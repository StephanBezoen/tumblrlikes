package nl.acidcats.tumblrlikes.core.usecases.photos;

import javax.inject.Inject;

import nl.acidcats.tumblrlikes.core.models.Photo;
import nl.acidcats.tumblrlikes.core.repositories.PhotoDataRepository;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created on 03/10/2018.
 */
public class UpdatePhotoPropertyUseCaseImpl implements UpdatePhotoPropertyUseCase {

    private PhotoDataRepository _photoDataRepository;

    @Inject
    public UpdatePhotoPropertyUseCaseImpl(PhotoDataRepository photoDataRepository) {
        _photoDataRepository = photoDataRepository;
    }

    @Override
    public Observable<Photo> updateLike(long id, boolean isLiked) {
        return Observable
                .fromCallable(() -> {
                    _photoDataRepository.setPhotoLiked(id, isLiked);

                    return _photoDataRepository.getPhotoById(id);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Photo> updateFavorite(long id, boolean isFavorite) {
        return Observable
                .fromCallable(() -> {
                    _photoDataRepository.setPhotoFavorite(id, isFavorite);

                    if (isFavorite) {
                        _photoDataRepository.setPhotoLiked(id, true);
                    }

                    return _photoDataRepository.getPhotoById(id);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Photo> setHidden(long id) {
        return Observable
                .fromCallable(() -> {
                    _photoDataRepository.setPhotoFavorite(id, false);
                    _photoDataRepository.setPhotoLiked(id, false);

                    _photoDataRepository.hidePhoto(id);

                    return _photoDataRepository.getPhotoById(id);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
