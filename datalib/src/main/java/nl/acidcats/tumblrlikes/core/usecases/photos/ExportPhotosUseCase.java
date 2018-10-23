package nl.acidcats.tumblrlikes.core.usecases.photos;

import rx.Observable;

/**
 * Created on 23/10/2018.
 */
public interface ExportPhotosUseCase {
    Observable<Boolean> exportPhotos(String filename);
}
