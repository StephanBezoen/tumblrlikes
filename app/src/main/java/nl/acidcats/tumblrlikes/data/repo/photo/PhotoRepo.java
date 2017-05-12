package nl.acidcats.tumblrlikes.data.repo.photo;

import android.support.annotation.Nullable;

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

    void markAsCached(PhotoEntity photo, String path);

    void startPhotoView(String url);

    void endPhotoView (@Nullable String url);

    void likePhoto(long id);

    void unlikePhoto(long id);

    void setPhotoFavorite(long id, boolean isFavorite);

    void setPhotoHidden(long id);

    @Nullable
    PhotoEntity getPhotoById(long id);
}
