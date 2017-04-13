package com.mediamonks.mylikes.data.repo.photo;

import com.mediamonks.mylikes.data.repo.photo.store.PhotoStore;
import com.mediamonks.mylikes.data.vo.db.PhotoEntity;

import java.util.List;

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
    public void storePhotos(List<PhotoEntity> photos) {
        _photoStore.storePhotos(photos);
    }

    @Override
    public PhotoEntity getRandomPhoto() {
        return _photoStore.getRandomPhoto();
    }
}
