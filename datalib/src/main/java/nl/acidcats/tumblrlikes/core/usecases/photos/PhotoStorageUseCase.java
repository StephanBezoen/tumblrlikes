package nl.acidcats.tumblrlikes.core.usecases.photos;

import rx.Observable;

/**
 * Created on 17/10/2018.
 */
public interface PhotoStorageUseCase {
    Observable<Long> getPhotoCount();
}
