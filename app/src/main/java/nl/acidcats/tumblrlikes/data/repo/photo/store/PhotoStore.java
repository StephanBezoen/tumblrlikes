package nl.acidcats.tumblrlikes.data.repo.photo.store;

import java.util.List;

import nl.acidcats.tumblrlikes.data.vo.db.PhotoEntity;

/**
 * Created by stephan on 11/04/2017.
 */

public interface PhotoStore {
    boolean hasPhoto(long postId);

    void storePhotos(List<PhotoEntity> photos);

    long getPhotoCount();

    PhotoEntity getRandomPhoto();
}
