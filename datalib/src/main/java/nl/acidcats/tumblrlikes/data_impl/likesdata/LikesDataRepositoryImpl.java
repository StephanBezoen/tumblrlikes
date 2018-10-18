package nl.acidcats.tumblrlikes.data_impl.likesdata;

import java.util.List;

import javax.inject.Inject;

import nl.acidcats.tumblrlikes.core.models.Photo;
import nl.acidcats.tumblrlikes.core.repositories.LikesDataRepository;
import nl.acidcats.tumblrlikes.data_impl.likesdata.gateway.LikesDataGateway;
import nl.acidcats.tumblrlikes.data_impl.likesdata.models.TumblrLikeVO;
import nl.acidcats.tumblrlikes.datalib.BuildConfig;
import retrofit2.HttpException;
import rx.Observable;

/**
 * Created by stephan on 28/03/2017.
 */

public class LikesDataRepositoryImpl implements LikesDataRepository {
    private static final String TAG = LikesDataRepositoryImpl.class.getSimpleName();

    private final LikesDataGateway _likesDataGateway;
    private final boolean _debug = BuildConfig.DEBUG;
    private final TumblrLikeTransformer _transformer;
    private boolean _isLoadComplete;
    private long _timeOfLastLike;

    @Inject
    public LikesDataRepositoryImpl(LikesDataGateway likesDataGateway) {
        _likesDataGateway = likesDataGateway;

        _transformer = new TumblrLikeTransformer();
    }

    @Override
    public Observable<List<Photo>> getLikedPhotos(String blogName, int count, long beforeTime) {
        return _likesDataGateway
                .getLikes(blogName, count, beforeTime)
                .doOnError(throwable -> new LoadLikesException(((HttpException) throwable).code()))
                .map(this::checkHasMore)
                .flatMapIterable(tumblrLikeVOs -> tumblrLikeVOs)
                .filter(TumblrLikeVO::isPhoto)
                .map(_transformer::transformToPhotos);
    }

    private List<TumblrLikeVO> checkHasMore(List<TumblrLikeVO> tumblrLikeVOs) {
        if (tumblrLikeVOs.size() == 0) {
            _isLoadComplete = true;
        } else {
            _isLoadComplete = false;

            _timeOfLastLike = tumblrLikeVOs.get(tumblrLikeVOs.size() - 1).timestamp();
        }

        return tumblrLikeVOs;
    }

    @Override
    public boolean isLoadComplete() {
        return _isLoadComplete;
    }

    @Override
    public long getLastLikeTime() {
        return _timeOfLastLike;
    }

}
