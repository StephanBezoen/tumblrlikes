package nl.acidcats.tumblrlikes.data.repo.photo;

import java.util.List;

import nl.acidcats.tumblrlikes.data.vo.db.PhotoEntity;

/**
 * Created by stephan on 11/04/2017.
 */

public interface PhotoRepo {
    boolean hasPhoto(long postId);

    List<PhotoEntity> storePhotos(List<PhotoEntity> photos);

    long getPhotoCount();

    PhotoEntity getRandomPhoto();

    boolean hasUncachedPhotos();

    PhotoEntity getNextUncachedPhoto();
}
