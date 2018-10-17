package nl.acidcats.tumblrlikes.core.usecases.photos;

import rx.Observable;

/**
 * Created on 17/10/2018.
 */
public interface PhotoViewUseCase {

    Observable<Boolean> startPhotoView(long id, long currentTime);

    Observable<Boolean> endPhotoView(long id, long currentTime);
}
