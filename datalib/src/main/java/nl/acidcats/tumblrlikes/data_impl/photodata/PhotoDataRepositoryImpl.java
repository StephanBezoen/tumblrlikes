package nl.acidcats.tumblrlikes.data_impl.photodata;

import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import nl.acidcats.tumblrlikes.core.constants.FilterType;
import nl.acidcats.tumblrlikes.core.models.Photo;
import nl.acidcats.tumblrlikes.core.repositories.PhotoDataRepository;
import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.PhotoDataGateway;
import nl.acidcats.tumblrlikes.datalib.BuildConfig;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by stephan on 11/04/2017.
 */

public class PhotoDataRepositoryImpl implements PhotoDataRepository {
    private static final String TAG = PhotoDataRepositoryImpl.class.getSimpleName();

    private PhotoDataGateway _photoDataGateway;
    private long _startViewTime;
    private long _currentPhotoId;
    private final boolean _debug = BuildConfig.DEBUG;

    @Inject
    public PhotoDataRepositoryImpl(PhotoDataGateway photoDataGateway) {
        _photoDataGateway = photoDataGateway;
    }

    @Override
    public boolean hasPhoto(long postId) {
        return _photoDataGateway.hasPhoto(postId);
    }

    @Override
    public List<Photo> storePhotos(List<Photo> photos) {
        _photoDataGateway.storePhotos(photos);

        return photos;
    }

    @Override
    public long getPhotoCount() {
        return _photoDataGateway.getPhotoCount();
    }

    @Override
    public Photo getNextPhoto() {
        return _photoDataGateway.getNextPhoto();
    }

    @Override
    public boolean hasUncachedPhotos() {
        return _photoDataGateway.hasUncachedPhotos();
    }

    @Override
    public Photo getNextUncachedPhoto() {
        return _photoDataGateway.getUncachedPhoto();
    }

    @Override
    public void markAsCached(long id, String path) {
        _photoDataGateway.setPhotoCached(id, true, path);
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

                _photoDataGateway.setPhotoCached(photo.id(), false, null);
            }
        }

        return null;
    }

    @Override
    public Observable<Void> removeCachedHiddenPhotos() {
        return Observable
                .just(_photoDataGateway.getCachedHiddenPhotos())
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

        _photoDataGateway.addPhotoViewTime(id, SystemClock.elapsedRealtime() - _startViewTime);
    }

    @Override
    public void setPhotoLiked(long id, boolean isLiked) {
        _photoDataGateway.setPhotoLiked(id, isLiked);
    }

    @Override
    public void setPhotoFavorite(long id, boolean isFavorite) {
        _photoDataGateway.setPhotoFavorite(id, isFavorite);
    }

    @Override
    public void hidePhoto(long id) {
        _photoDataGateway.setPhotoHidden(id);

        Photo photo = _photoDataGateway.getPhotoById(id);
        if (photo != null) {
            uncachePhoto(photo);
        }
    }

    @Override
    @Nullable
    public Photo getPhotoById(long id) {
        return _photoDataGateway.getPhotoById(id);
    }

    @Override
    public void setFilterType(FilterType filterType) {
        _photoDataGateway.setFilterType(filterType);
    }

    @Override
    public FilterType getFilterType() {
        return _photoDataGateway.getFilterType();
    }

    @Override
    public boolean isPhotoCacheMissing(Photo photo) {
        String filePath = photo.filePath();

        if (filePath != null) {
            File file = new File(filePath);
            return !file.exists();
        }

        return false;
    }

    @Override
    public List<Long> setPhotosUncached(final List<Long> idList) {
        _photoDataGateway.setPhotosCached(idList, false);

        return idList;
    }

    @Override
    public List<Photo> getCachedPhotos() {
        return _photoDataGateway.getCachedPhotos();
    }
}