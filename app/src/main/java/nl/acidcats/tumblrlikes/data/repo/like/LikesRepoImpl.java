package nl.acidcats.tumblrlikes.data.repo.like;

import com.pixplicity.easyprefs.library.Prefs;

import java.util.Date;
import java.util.List;

import nl.acidcats.tumblrlikes.data.constants.PrefKeys;
import nl.acidcats.tumblrlikes.data.repo.like.store.LikesStore;
import nl.acidcats.tumblrlikes.data.vo.tumblr.TumblrLikeVO;
import rx.Observable;

/**
 * Created by stephan on 28/03/2017.
 */

public class LikesRepoImpl implements LikesRepo {
    private static final String TAG = LikesRepoImpl.class.getSimpleName();

    private static final long TIME_BETWEEN_CHECKS_MS = 24L * 60L * 60L * 1000L; // 24 hours

    private final LikesStore _netStore;
    private boolean _hasMore;
    private long _lastTime;

    public LikesRepoImpl(LikesStore netStore) {
        _netStore = netStore;
    }

    @Override
    public Observable<List<TumblrLikeVO>> getLikes(String blogName, int count, long beforeTime) {
        return _netStore.getLikes(blogName, count, beforeTime).doOnNext(tumblrLikeVOs -> {
            if (tumblrLikeVOs.size() == 0) {
                _hasMore = false;
            } else {
                _hasMore = true;

                _lastTime = tumblrLikeVOs.get(tumblrLikeVOs.size() - 1).timestamp();
            }
        });
    }

    @Override
    public boolean hasMoreLikes() {
        return _hasMore;
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
}
