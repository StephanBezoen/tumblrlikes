package nl.acidcats.tumblrlikes.data.repo.photo;

import java.util.List;

import javax.inject.Inject;

import nl.acidcats.tumblrlikes.data.repo.photo.store.PhotoStore;
import nl.acidcats.tumblrlikes.data.vo.db.PhotoEntity;

/**
 * Created by stephan on 11/04/2017.
 */

public class PhotoRepoImpl implements PhotoRepo {
    private static final String TAG = PhotoRepoImpl.class.getSimpleName();

    @Inject
    PhotoStore _photoStore;

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
        return _photoStore.getRandomPhoto();
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
}
