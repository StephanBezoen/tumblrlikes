package nl.acidcats.tumblrlikes.core.repositories.gateways;

import java.util.List;

import nl.acidcats.tumblrlikes.core.models.tumblr.TumblrLikeVO;
import rx.Observable;

/**
 * Created by stephan on 28/03/2017.
 */

public interface LikesDataGateway {
    Observable<List<TumblrLikeVO>> getLikes(String blogName, int count, long beforeTime);
}
