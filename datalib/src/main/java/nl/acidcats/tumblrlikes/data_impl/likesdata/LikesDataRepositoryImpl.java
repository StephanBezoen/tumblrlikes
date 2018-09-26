package nl.acidcats.tumblrlikes.data_impl.likesdata;

import android.util.Log;

import com.pixplicity.easyprefs.library.Prefs;

import java.util.Date;
import java.util.List;

import nl.acidcats.tumblrlikes.core.repositories.LikesDataRepository;
import nl.acidcats.tumblrlikes.data_impl.appdata.PrefKeys;
import nl.acidcats.tumblrlikes.core.repositories.gateways.LikesDataGateway;
import nl.acidcats.tumblrlikes.core.models.tumblr.TumblrLikeVO;
import nl.acidcats.tumblrlikes.datalib.BuildConfig;
import retrofit2.HttpException;
import rx.Observable;

/**
 * Created by stephan on 28/03/2017.
 */

public class LikesDataRepositoryImpl implements LikesDataRepository {
    private static final String TAG = LikesDataRepositoryImpl.class.getSimpleName();

    private static final long TIME_BETWEEN_CHECKS_MS = 24L * 60L * 60L * 1000L; // 24 hours

    private final LikesDataGateway _netStore;
    private final boolean _debug = BuildConfig.DEBUG;
    private boolean _hasMore;
    private long _lastTime;

    public LikesDataRepositoryImpl(LikesDataGateway netStore) {
        _netStore = netStore;
    }

    @Override
    public Observable<List<TumblrLikeVO>> getLikes(String blogName, int count, long beforeTime) {
        return _netStore
                .getLikes(blogName, count, beforeTime)
                .doOnNext(tumblrLikeVOs -> {
                    if (tumblrLikeVOs.size() == 0) {
                        _hasMore = false;
                    } else {
                        _hasMore = true;

                        _lastTime = tumblrLikeVOs.get(tumblrLikeVOs.size() - 1).timestamp();
                    }
                })
                .doOnError(throwable -> new LoadLikesException(((HttpException) throwable).code()));
    }

    @Override
    public boolean hasMoreLikes() {
        // check if there's more to load, based on whether previous request returned empty, and whether last item loaded had timestamp after most recent
        // check time
        return _hasMore && (_lastTime > getMostRecentCheckTime());
    }

    @Override
    public long getLastLikeTime() {
        return _lastTime;
    }

    @Override
    public void setCheckComplete() {
        Prefs.putLong(PrefKeys.KEY_LATEST_CHECK_TIMESTAMP, new Date().getTime());
    }

    @Override
    public long getMostRecentCheckTime() {
        return Prefs.getLong(PrefKeys.KEY_LATEST_CHECK_TIMESTAMP, 0L);
    }

    @Override
    public boolean isTimeToCheck() {
        long timeSinceLastCheck = new Date().getTime() - getMostRecentCheckTime();
        return timeSinceLastCheck > TIME_BETWEEN_CHECKS_MS;
    }

    @Override
    public void reset() {
        if (_debug) Log.d(TAG, "reset: ");

        Prefs.putLong(PrefKeys.KEY_LATEST_CHECK_TIMESTAMP, 0L);
    }
}
