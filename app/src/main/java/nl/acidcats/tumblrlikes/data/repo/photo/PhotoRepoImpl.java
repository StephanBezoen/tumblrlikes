package nl.acidcats.tumblrlikes.data.repo.photo;

import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import nl.acidcats.tumblrlikes.BuildConfig;
import nl.acidcats.tumblrlikes.data.constants.FilterType;
import nl.acidcats.tumblrlikes.data.repo.photo.store.PhotoStore;
import nl.acidcats.tumblrlikes.data.vo.db.PhotoEntity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
    public PhotoEntity getNextPhoto() {
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

    public Void uncachePhoto(PhotoEntity photo) {
        if (_debug) Log.d(TAG, "uncachePhoto: " + photo.getFilePath());

        File file = new File(photo.getFilePath());
        if (file.exists()) {
            if (_debug) Log.d(TAG, "uncachePhoto: file exists");

            if (file.delete()) {
                if (_debug) Log.d(TAG, "uncachePhoto: file deleted");

                photo.setIsCached(false);

                _photoStore.storePhoto(photo);
            }
        }

        return null;
    }

    @Override
    public Observable<Void> removeCachedHiddenPhotos() {
        return Observable
                .just(_photoStore.getCachedHiddenPhotos())
                .subscribeOn(Schedulers.io())
                .flatMapIterable(photoEntities -> photoEntities)
                .map(this::uncachePhoto)
                .toList()
                .flatMap(photoEntities -> Observable.just(null));
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

        PhotoEntity photo = _photoStore.getPhotoById(id);
        if (photo != null) {
            uncachePhoto(photo);
        }
    }

    @Override
    @Nullable
    public PhotoEntity getPhotoById(long id) {
        return _photoStore.getPhotoById(id);
    }

    @Override
    public void setFilterType(FilterType filterType) {
        _photoStore.setFilterType(filterType);
    }

    @Override
    public FilterType getFilterType() {
        return _photoStore.getFilterType();
    }
}
