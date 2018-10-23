package nl.acidcats.tumblrlikes.data_impl.photodata.gateway;

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

    @Nullable
    Photo getPhotoById(long id);

    void setPhotoLiked(long id, boolean isLiked);

    void setPhotoFavorite(long id, boolean isFavorite);

    void setPhotoHidden(long id);

    void setPhotoCached(long id, boolean isCached, @Nullable String filepath);

    void setPhotosCached(List<Long> ids, boolean isCached);

    void addPhotoViewTime(long id, long timeInMs);

    void initFilter(FilterType filterType);

    List<Photo> getCachedPhotos();

    List<Photo> getCachedHiddenPhotos();

    boolean hasUncachedPhotos();

    @Nullable
    Photo getUncachedPhoto();

    List<Photo> getAllPhotos();
}
