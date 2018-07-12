package nl.acidcats.tumblrlikes.data.repo.photo;

import android.support.annotation.Nullable;

import java.util.List;

import nl.acidcats.tumblrlikes.data.constants.FilterType;
import nl.acidcats.tumblrlikes.data.vo.Photo;
import rx.Observable;

/**
 * Created by stephan on 11/04/2017.
 */

public interface PhotoRepo {
    boolean hasPhoto(long postId);

    List<Photo> storePhotos(List<Photo> photos);

    long getPhotoCount();

    Photo getNextPhoto();

    boolean hasUncachedPhotos();

    Photo getNextUncachedPhoto();

    void markAsCached(long id, String path);

    Observable<Void> removeCachedHiddenPhotos();

    void startPhotoView(long id);

    void endPhotoView(long id);

    void likePhoto(long id);

    void unlikePhoto(long id);

    void setPhotoFavorite(long id, boolean isFavorite);

    void setPhotoHidden(long id);

    @Nullable
    Photo getPhotoById(long id);

    void setFilterType(FilterType filterType);

    FilterType getFilterType();

    Observable<Integer> checkCachedPhotos();
}
