package nl.acidcats.tumblrlikes.data.repo.photo.store;

import android.support.annotation.Nullable;

import java.util.List;

import nl.acidcats.tumblrlikes.data.vo.db.PhotoEntity;

/**
 * Created by stephan on 11/04/2017.
 */

public interface PhotoStore {
    boolean hasPhoto(long postId);

    void storePhotos(List<PhotoEntity> photos);

    long getPhotoCount();

    @Nullable
    PhotoEntity getRandomPhoto();

    boolean hasUncachedPhotos();

    @Nullable
    PhotoEntity getNextUncachedPhoto();

    void storePhoto(PhotoEntity photo);

    void addViewTime(PhotoEntity photo, long timeInMs);

    @Nullable
    PhotoEntity getPhotoByUrl (String url);
}
