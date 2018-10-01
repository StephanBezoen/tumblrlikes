package nl.acidcats.tumblrlikes.data_impl.likesdata.gateway;

import java.util.List;

import nl.acidcats.tumblrlikes.data_impl.likesdata.models.TumblrLikeVO;
import rx.Observable;

/**
 * Created by stephan on 28/03/2017.
 */

public interface LikesDataGateway {
    Observable<List<TumblrLikeVO>> getLikes(String blogName, int count, long beforeTime);
}
