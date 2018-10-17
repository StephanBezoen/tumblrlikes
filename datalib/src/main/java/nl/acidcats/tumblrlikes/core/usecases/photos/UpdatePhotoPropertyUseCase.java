package nl.acidcats.tumblrlikes.core.usecases.photos;

import nl.acidcats.tumblrlikes.core.models.Photo;
import rx.Observable;

/**
 * Created on 03/10/2018.
 */
public interface UpdatePhotoPropertyUseCase {
    Observable<Photo> updateLike(long id, boolean isLiked);

    Observable<Photo> updateFavorite(long id, boolean isFavorite);

    Observable<Photo> setHidden(long id);
}
