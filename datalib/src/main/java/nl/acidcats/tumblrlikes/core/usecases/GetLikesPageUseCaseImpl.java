package nl.acidcats.tumblrlikes.core.usecases;

import java.util.List;

import javax.inject.Inject;

import nl.acidcats.tumblrlikes.core.models.Photo;
import nl.acidcats.tumblrlikes.core.models.tumblr.TumblrLikeVO;
import nl.acidcats.tumblrlikes.core.repositories.AppDataRepository;
import nl.acidcats.tumblrlikes.core.repositories.LikesDataRepository;
import nl.acidcats.tumblrlikes.core.repositories.PhotoDataRepository;
import nl.acidcats.tumblrlikes.util.PhotoUtil;
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
    public Observable<List<Photo>> getPageOfLikesBefore(long timestamp) {
        return _likesDataRepository.getLikes(_appDataRepository.getTumblrBlog(), 20, timestamp)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapIterable(tumblrLikeVOs -> tumblrLikeVOs)
                .filter(TumblrLikeVO::isPhoto)
                .filter(tumblrLikeVO -> !_photoDataRepository.hasPhoto(tumblrLikeVO.id()))
                .map(PhotoUtil::toPhotos)
                .flatMapIterable(photos -> photos)
                .toList()
                .map(_photoDataRepository::storePhotos);
    }
}
