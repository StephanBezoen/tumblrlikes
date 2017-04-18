package nl.acidcats.tumblrlikes.data.repo.like;

import java.util.List;

import nl.acidcats.tumblrlikes.data.vo.tumblr.TumblrLikeVO;
import rx.Observable;

/**
 * Created by stephan on 28/03/2017.
 */

public interface LikesRepo {
    Observable<List<TumblrLikeVO>> getLikes(String blogName, int count, long beforeTime);

    boolean hasMoreLikes();

    long getLastLikeTime();

    void setCheckComplete();

    long getMostRecentCheckTime();

    boolean isTimeToCheck();

    void reset();
}
