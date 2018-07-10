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
import nl.acidcats.tumblrlikes.data.vo.Photo;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by stephan on 11/04/2017.
 */

public class PhotoRepoImpl implements PhotoRepo {
    private static final String TAG = PhotoRepoImpl.class.getSimpleName();

    @Inject
    PhotoStore _photoStore;

    private long _startViewTime;
    private long _currentPhotoId;
    private final boolean _debug = BuildConfig.DEBUG;

    @Inject
    public PhotoRepoImpl() {
    }

    @Override
    public boolean hasPhoto(long postId) {
        return _photoStore.hasPhoto(postId);
    }

    @Override
    public List<Photo> storePhotos(List<Photo> photos) {
        _photoStore.storePhotos(photos);

        return photos;
    }

    @Override
    public long getPhotoCount() {
        return _photoStore.getPhotoCount();
    }

    @Override
    public Photo getNextPhoto() {
        return _photoStore.getNextPhoto();
    }

    @Override
    public boolean hasUncachedPhotos() {
        return _photoStore.hasUncachedPhotos();
    }

    @Override
    public Photo getNextUncachedPhoto() {
        return _photoStore.getNextUncachedPhoto();
    }

    @Override
    public void markAsCached(long id, String path) {
        _photoStore.setAsCached(id, path);
    }

    private Void uncachePhoto(Photo photo) {
        if (_debug) Log.d(TAG, "uncachePhoto: " + photo.filePath());

        if (photo.filePath() == null) return null;

        //noinspection ConstantConditions
        File file = new File(photo.filePath());
        if (file.exists()) {
            if (_debug) Log.d(TAG, "uncachePhoto: file exists");

            if (file.delete()) {
                if (_debug) Log.d(TAG, "uncachePhoto: file deleted");

                _photoStore.setAsUncached(photo.id());
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
    public void startPhotoView(long id) {
        _currentPhotoId = id;

        _startViewTime = SystemClock.elapsedRealtime();
    }

    @Override
    public void endPhotoView(long id) {
        if (id == 0 || id != _currentPhotoId) return;

        _photoStore.addViewTime(id, SystemClock.elapsedRealtime() - _startViewTime);
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

        Photo photo = _photoStore.getPhotoById(id);
        if (photo != null) {
            uncachePhoto(photo);
        }
    }

    @Override
    @Nullable
    public Photo getPhotoById(long id) {
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


    @Override
    public Observable<Integer> checkCachedPhotos() {
        return Observable
                .fromCallable(() -> _photoStore.getCachedPhotos())
                .subscribeOn(Schedulers.io())
                .flatMapIterable(photoEntities -> photoEntities)
                .map(this::checkPhotoCache)
                .toList()
                .flatMap(this::countUncached);
    }

    private Integer checkPhotoCache(Photo photo) {
        String filePath = photo.filePath();

        if (filePath != null) {
            File file = new File(filePath);
            if (!file.exists()) {
                _photoStore.setAsUncached(photo.id());

                return 1;
            }
        }

        return 0;
    }

    private Observable<Integer> countUncached(List<Integer> integers) {
        int sum = 0;
        for (Integer integer : integers) {
            sum += integer;
        }

        return Observable.just(sum);
    }
}
