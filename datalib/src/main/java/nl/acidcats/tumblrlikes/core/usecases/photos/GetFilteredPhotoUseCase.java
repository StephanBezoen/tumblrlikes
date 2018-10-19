package nl.acidcats.tumblrlikes.core.usecases.photos;

import nl.acidcats.tumblrlikes.core.models.Photo;
import rx.Observable;

/**
 * Created on 19/10/2018.
 */
public interface GetFilteredPhotoUseCase {
    Observable<Photo> getNextFilteredPhoto();
}
