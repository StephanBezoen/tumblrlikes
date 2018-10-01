package nl.acidcats.tumblrlikes.core.repositories;

import java.util.List;

import nl.acidcats.tumblrlikes.core.models.Photo;
import rx.Observable;

/**
 * Created by stephan on 28/03/2017.
 */

public interface LikesDataRepository {
    Observable<List<Photo>> getLikedPhotos(String blogName, int count, long beforeTime);

    boolean hasMoreLikes(long mostRecentCheckTime);

    long getLastLikeTime();
}
