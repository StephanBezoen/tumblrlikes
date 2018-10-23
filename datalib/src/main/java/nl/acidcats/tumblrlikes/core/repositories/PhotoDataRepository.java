package nl.acidcats.tumblrlikes.core.repositories;

import android.support.annotation.Nullable;

import java.util.List;

import nl.acidcats.tumblrlikes.core.constants.FilterType;
import nl.acidcats.tumblrlikes.core.models.Photo;
import rx.Observable;

/**
 * Created by stephan on 11/04/2017.
 */

public interface PhotoDataRepository {
    boolean hasPhoto(long postId);

    List<Photo> storePhotos(List<Photo> photos);

    List<Photo> getAllPhotos();

    long getPhotoCount();

    Photo getNextPhoto();

    void setPhotoViewStartTime(long id, long currentTime);

    void updatePhotoViewTime(long id, long currentTime);

    void setPhotoLiked(long id, boolean isLiked);

    void setPhotoFavorite(long id, boolean isFavorite);

    void hidePhoto(long id);

    @Nullable
    Photo getPhotoById(long id);

    void setFilterType(FilterType filterType);

    FilterType getFilterType();

    boolean hasUncachedPhotos();

    Photo getNextUncachedPhoto();

    void markAsCached(long id, String path);

    Observable<Void> removeCachedHiddenPhotos();

    boolean isPhotoCacheMissing(Photo photo);

    List<Long> setPhotosUncached(final List<Long> idList);

    List<Photo> getCachedPhotos();
}
