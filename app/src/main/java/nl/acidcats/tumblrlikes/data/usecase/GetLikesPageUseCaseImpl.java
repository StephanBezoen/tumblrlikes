package nl.acidcats.tumblrlikes.data.usecase;

import java.util.List;

import javax.inject.Inject;

import nl.acidcats.tumblrlikes.core.repositories.AppDataRepository;
import nl.acidcats.tumblrlikes.core.repositories.LikesDataRepository;
import nl.acidcats.tumblrlikes.core.repositories.PhotoDataRepository;
import nl.acidcats.tumblrlikes.util.PhotoUtil;
import nl.acidcats.tumblrlikes.core.models.Photo;
import nl.acidcats.tumblrlikes.core.models.tumblr.TumblrLikeVO;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by stephan on 13/04/2017.
 */

public class GetLikesPageUseCaseImpl implements GetLikesPageUseCase {
    private static final String TAG = GetLikesPageUseCaseImpl.class.getSimpleName();

    @Inject
    LikesDataRepository _likesRepo;
    @Inject
    PhotoDataRepository _photoRepo;
    @Inject
    AppDataRepository _appRepo;

    @Inject
    public GetLikesPageUseCaseImpl() {
    }

    @Override
    public Observable<List<Photo>> getPageOfLikesBefore(long timestamp) {
        return _likesRepo.getLikes(_appRepo.getTumblrBlog(), 20, timestamp)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapIterable(tumblrLikeVOs -> tumblrLikeVOs)
                .filter(TumblrLikeVO::isPhoto)
                .filter(tumblrLikeVO -> !_photoRepo.hasPhoto(tumblrLikeVO.id()))
                .map(PhotoUtil::toPhotos)
                .flatMapIterable(photos -> photos)
                .toList()
                .map(_photoRepo::storePhotos);
    }
}
