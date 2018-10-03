package nl.acidcats.tumblrlikes.core.usecases.photos;

import rx.Observable;

/**
 * Created on 01/10/2018.
 */
public interface PhotoCacheUseCase {
    Observable<Void> removeCachedHiddenPhotos();

    Observable<Integer> checkCachedPhotos();
}
