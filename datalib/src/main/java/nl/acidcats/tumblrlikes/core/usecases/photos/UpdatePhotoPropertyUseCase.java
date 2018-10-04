package nl.acidcats.tumblrlikes.core.usecases.photos;

import rx.Observable;

/**
 * Created on 03/10/2018.
 */
public interface UpdatePhotoPropertyUseCase {
    Observable<Boolean> updateLike(long id, boolean isLiked);

    Observable<Boolean> updateFavorite(long id, boolean isFavorite);

    Observable<Boolean> setHidden(long id);
}
