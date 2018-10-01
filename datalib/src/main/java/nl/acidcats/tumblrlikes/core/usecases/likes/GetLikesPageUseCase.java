package nl.acidcats.tumblrlikes.core.usecases.likes;

import java.util.List;

import nl.acidcats.tumblrlikes.core.constants.LoadLikesMode;
import nl.acidcats.tumblrlikes.core.models.Photo;
import rx.Observable;

/**
 * Created by stephan on 13/04/2017.
 */

public interface GetLikesPageUseCase {
    Observable<List<Photo>> loadLikesPage(LoadLikesMode mode);

    Observable<Boolean> checkLoadLikesComplete();
}
