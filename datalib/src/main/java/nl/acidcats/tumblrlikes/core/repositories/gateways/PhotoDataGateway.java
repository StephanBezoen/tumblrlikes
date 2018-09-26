package nl.acidcats.tumblrlikes.core.repositories.gateways;

import android.support.annotation.Nullable;

import java.util.List;

import nl.acidcats.tumblrlikes.core.constants.FilterType;
import nl.acidcats.tumblrlikes.core.models.Photo;

/**
 * Created by stephan on 11/04/2017.
 */

public interface PhotoDataGateway {
    boolean hasPhoto(long postId);

    void storePhotos(List<Photo> photos);

    long getPhotoCount();

    @Nullable
    Photo getNextPhoto();

    boolean hasUncachedPhotos();

    @Nullable
    Photo getNextUncachedPhoto();

    void setAsCached(long id, String filePath);

    void setAsUncached(long id);

    void setAsUncached(List<Long> ids);

    void addViewTime(long id, long timeInMs);

    @Nullable
    Photo getPhotoById(long id);

    void likePhoto(long id);

    void unlikePhoto(long id);

    void setPhotoFavorite(long id, boolean isFavorite);

    void setPhotoHidden(long id);

    List<Photo> getCachedHiddenPhotos();

    void setFilterType(FilterType filterType);

    FilterType getFilterType();

    List<Photo> getCachedPhotos();
}
