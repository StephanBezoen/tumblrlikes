package nl.acidcats.tumblrlikes.core.usecases.likes;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import nl.acidcats.tumblrlikes.core.constants.LoadLikesMode;
import nl.acidcats.tumblrlikes.core.models.Photo;
import nl.acidcats.tumblrlikes.core.repositories.AppDataRepository;
import nl.acidcats.tumblrlikes.core.repositories.LikesDataRepository;
import nl.acidcats.tumblrlikes.core.repositories.PhotoDataRepository;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by stephan on 13/04/2017.
 */

public class GetLikesPageUseCaseImpl implements GetLikesPageUseCase {
    private static final String TAG = GetLikesPageUseCaseImpl.class.getSimpleName();

    private LikesDataRepository _likesDataRepository;
    private PhotoDataRepository _photoDataRepository;
    private AppDataRepository _appDataRepository;

    @Inject
    public GetLikesPageUseCaseImpl(LikesDataRepository likesDataRepository,
                                   PhotoDataRepository photoDataRepository,
                                   AppDataRepository appDataRepository) {
        _likesDataRepository = likesDataRepository;
        _photoDataRepository = photoDataRepository;
        _appDataRepository = appDataRepository;
    }

    @Override
    public Observable<List<Photo>> loadLikesPage(LoadLikesMode mode) {
        long timestamp;
        switch (mode) {
            case FRESH:
                timestamp = new Date().getTime();
                break;
            case CONTINUED:
                timestamp = _likesDataRepository.getLastLikeTime();
                break;
            default:
                throw new RuntimeException("Unsupported value for mode: " + mode);
        }

        return _likesDataRepository.getLikedPhotos(_appDataRepository.getTumblrBlog(), 20, timestamp)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapIterable(photos -> photos)
                .filter(photo -> !_photoDataRepository.hasPhoto(photo.tumblrId()))
                .toList()
                .map(_photoDataRepository::storePhotos);
    }

    @Override
    public Observable<Boolean> checkLoadLikesComplete() {
        boolean isComplete = !_likesDataRepository.hasMoreLikes(_appDataRepository.getMostRecentCheckTime());

        if (isComplete) {
            _appDataRepository.setCheckComplete();
        }

        return Observable.just(isComplete);
    }
}
