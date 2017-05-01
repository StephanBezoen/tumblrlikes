package nl.acidcats.tumblrlikes.data.usecase;

import java.util.List;

import javax.inject.Inject;

import nl.acidcats.tumblrlikes.BuildConfig;
import nl.acidcats.tumblrlikes.data.repo.app.AppRepo;
import nl.acidcats.tumblrlikes.data.repo.like.LikesRepo;
import nl.acidcats.tumblrlikes.data.repo.photo.PhotoRepo;
import nl.acidcats.tumblrlikes.data.util.PhotoUtil;
import nl.acidcats.tumblrlikes.data.vo.db.PhotoEntity;
import nl.acidcats.tumblrlikes.data.vo.tumblr.TumblrLikeVO;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by stephan on 13/04/2017.
 */

public class GetLikesPageUseCaseImpl implements GetLikesPageUseCase {
    private static final String TAG = GetLikesPageUseCaseImpl.class.getSimpleName();

    @Inject
    LikesRepo _likesRepo;
    @Inject
    PhotoRepo _photoRepo;
    @Inject
    AppRepo _appRepo;

    @Inject
    public GetLikesPageUseCaseImpl() {
    }

    @Override
    public Observable<List<PhotoEntity>> getPageOfLikesBefore(long timestamp) {
        return _likesRepo
                .getLikes(_appRepo.getTumblrBlog(), 20, timestamp)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapIterable(tumblrLikeVOs -> tumblrLikeVOs)
                .filter(TumblrLikeVO::isPhoto)
                .filter(tumblrLikeVO -> !_photoRepo.hasPhoto(tumblrLikeVO.id()))
                .map(PhotoUtil::toPhotoEntities)
                .flatMapIterable(photoEntities -> photoEntities)
                .toList()
                .map(_photoRepo::storePhotos);
    }
}
