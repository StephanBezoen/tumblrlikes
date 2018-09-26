package nl.acidcats.tumblrlikes.data_impl.photodata;

import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import nl.acidcats.tumblrlikes.core.repositories.PhotoDataRepository;
import nl.acidcats.tumblrlikes.core.constants.FilterType;
import nl.acidcats.tumblrlikes.core.repositories.gateways.PhotoDataGateway;
import nl.acidcats.tumblrlikes.core.models.Photo;
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
        return _photoDataGateway.getNextUncachedPhoto();
    }

    @Override
    public void markAsCached(long id, String path) {
        _photoDataGateway.setAsCached(id, path);
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

                _photoDataGateway.setAsUncached(photo.id());
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

        _photoDataGateway.addViewTime(id, SystemClock.elapsedRealtime() - _startViewTime);
    }

    @Override
    public void likePhoto(long id) {
        _photoDataGateway.likePhoto(id);
    }

    @Override
    public void unlikePhoto(long id) {
        _photoDataGateway.unlikePhoto(id);
    }

    @Override
    public void setPhotoFavorite(long id, boolean isFavorite) {
        _photoDataGateway.setPhotoFavorite(id, isFavorite);
    }

    @Override
    public void setPhotoHidden(long id) {
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
    public Observable<Integer> checkCachedPhotos() {
        return Observable
                .fromCallable(() -> _photoDataGateway.getCachedPhotos())
                .subscribeOn(Schedulers.io())
                .flatMapIterable(photoEntities -> photoEntities)
                .map(this::checkPhotoCache)
                .toList()
                .flatMap(this::unCache);
    }

    private Long checkPhotoCache(Photo photo) {
        String filePath = photo.filePath();

        if (filePath != null) {
            File file = new File(filePath);
            if (!file.exists()) {
                return photo.id();
            }
        }

        return -1L;
    }

    private Observable<Integer> unCache(final List<Long> idList) {
        int sum = 0;
        List<Long> allIds = new ArrayList<>();

        for (Long id : idList) {
            if (id == -1L) continue;

            allIds.add(id);
            sum++;
        }

        final int totalCount = allIds.size();
        final int maxPerPage = 500;
        final int pageCount = (int) Math.ceil((float) totalCount / (float) maxPerPage);

        for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
            final List<Long> ids = new ArrayList<>();

            final int baseIndex = pageIndex * maxPerPage;
            final int endIndex = Math.min(baseIndex + maxPerPage, totalCount);

            for (int idIndex = baseIndex; idIndex < endIndex; idIndex++) {
                ids.add(allIds.get(idIndex));
            }

            _photoDataGateway.setAsUncached(ids);
        }


        return Observable.just(sum);
    }
}
