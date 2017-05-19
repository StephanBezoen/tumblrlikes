package nl.acidcats.tumblrlikes.data.repo.photo;

import android.os.SystemClock;
import android.support.annotation.Nullable;

import java.util.List;

import javax.inject.Inject;

import nl.acidcats.tumblrlikes.BuildConfig;
import nl.acidcats.tumblrlikes.data.constants.FilterType;
import nl.acidcats.tumblrlikes.data.repo.photo.store.PhotoStore;
import nl.acidcats.tumblrlikes.data.vo.db.PhotoEntity;

/**
 * Created by stephan on 11/04/2017.
 */

public class PhotoRepoImpl implements PhotoRepo {
    private static final String TAG = PhotoRepoImpl.class.getSimpleName();

    @Inject
    PhotoStore _photoStore;

    private long _startViewTime;
    private String _currentUrl;
    private final boolean _debug = BuildConfig.DEBUG;

    @Inject
    public PhotoRepoImpl() {
    }

    @Override
    public boolean hasPhoto(long postId) {
        return _photoStore.hasPhoto(postId);
    }

    @Override
    public List<PhotoEntity> storePhotos(List<PhotoEntity> photos) {
        _photoStore.storePhotos(photos);

        return photos;
    }

    @Override
    public long getPhotoCount() {
        return _photoStore.getPhotoCount();
    }

    @Override
    public PhotoEntity getRandomPhoto() {
        return _photoStore.getNextPhoto();
    }

    @Override
    public boolean hasUncachedPhotos() {
        return _photoStore.hasUncachedPhotos();
    }

    @Override
    public PhotoEntity getNextUncachedPhoto() {
        return _photoStore.getNextUncachedPhoto();
    }

    @Override
    public void markAsCached(PhotoEntity photo, String path) {
        photo.setIsCached(true);
        photo.setFilePath(path);

        _photoStore.storePhoto(photo);
    }

    @Override
    public void startPhotoView(String url) {
        _currentUrl = url;

        _startViewTime = SystemClock.elapsedRealtime();
    }

    @Override
    public void endPhotoView(@Nullable String url) {
        if (url == null || !url.equals(_currentUrl)) return;

        PhotoEntity photo = _photoStore.getPhotoByPath(url);
        if (photo == null) {
            return;
        }

        long diff = SystemClock.elapsedRealtime() - _startViewTime;
        _photoStore.addViewTime(photo, diff);
    }

    @Override
    public void likePhoto(long id) {
        _photoStore.likePhoto(id);
    }

    @Override
    public void unlikePhoto(long id) {
        _photoStore.unlikePhoto(id);
    }

    @Override
    public void setPhotoFavorite(long id, boolean isFavorite) {
        _photoStore.setPhotoFavorite(id, isFavorite);
    }

    @Override
    public void setPhotoHidden(long id) {
        _photoStore.setPhotoHidden(id);
    }

    @Override
    @Nullable
    public PhotoEntity getPhotoById(long id) {
        return _photoStore.getPhotoById(id);
    }

    @Override
    public void setFilterType(FilterType filterType) {
        _filterType = filterType;

        _photoStore.setFilterType(filterType);
    }

    @Override
    public FilterType getFilterType() {
        return _photoStore.getFilterType();
    }
}
