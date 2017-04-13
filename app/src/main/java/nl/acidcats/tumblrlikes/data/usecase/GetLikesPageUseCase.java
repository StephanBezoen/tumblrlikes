package nl.acidcats.tumblrlikes.data.usecase;

import java.util.List;

import nl.acidcats.tumblrlikes.data.vo.db.PhotoEntity;
import rx.Observable;

/**
 * Created by stephan on 13/04/2017.
 */

public interface GetLikesPageUseCase {
    Observable<List<PhotoEntity>> getPageOfLikes(long timestamp);
}
