package nl.acidcats.tumblrlikes.data_impl.photodata;

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
    private FilterType _currentFilterType = FilterType.UNHIDDEN;

    @Inject
    public PhotoDataRepositoryImpl(PhotoDataGateway photoDataGateway) {
        _photoDataGateway = photoDataGateway;
        setFilterType(_currentFilterType);
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
    public List<Photo> getAllPhotos() {
        return _photoDataGateway.getAllPhotos();
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
        if (_debug) Log.d(TAG, "uncachePhoto: " + photo.getFilePath());

        if (photo.getFilePath() == null) return null;

        //noinspection ConstantConditions
        File file = new File(photo.getFilePath());
        if (file.exists()) {
            if (_debug) Log.d(TAG, "uncachePhoto: file exists");

            if (file.delete()) {
                if (_debug) Log.d(TAG, "uncachePhoto: file deleted");

                _photoDataGateway.setPhotoCached(photo.getId(), false, null);
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
    public void setPhotoViewStartTime(long id, long currentTime) {
        if (_debug) Log.d(TAG, "startPhotoView: id = " + id);

        _currentPhotoId = id;

        _startViewTime = currentTime;
    }

    @Override
    public void updatePhotoViewTime(long id, long currentTime) {
        if (_debug) Log.d(TAG, "endPhotoView: id = " + id + ", _currentPhotoId = " + _currentPhotoId);

        if (id == 0 || id != _currentPhotoId) {
            if (_debug) Log.d(TAG, "endPhotoView: no photo found to end");
            return;
        }

        _photoDataGateway.addPhotoViewTime(id, currentTime - _startViewTime);
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
        _currentFilterType = filterType;

        _photoDataGateway.initFilter(filterType);
    }

    @Override
    public FilterType getFilterType() {
        return _currentFilterType;
    }

    @Override
    public boolean isPhotoCacheMissing(Photo photo) {
        String filePath = photo.getFilePath();

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
