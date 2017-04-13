package nl.acidcats.tumblrlikes.data.repo.photo;

import java.util.List;

import nl.acidcats.tumblrlikes.data.repo.photo.store.PhotoStore;
import nl.acidcats.tumblrlikes.data.vo.db.PhotoEntity;

/**
 * Created by stephan on 11/04/2017.
 */

public class PhotoRepoImpl implements PhotoRepo {
    private static final String TAG = PhotoRepoImpl.class.getSimpleName();

    private PhotoStore _photoStore;

    public PhotoRepoImpl(PhotoStore photoStore) {
        _photoStore = photoStore;
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
}
