package com.mediamonks.mylikes.data.repo.photo.store;

import com.mediamonks.mylikes.data.vo.db.PhotoEntity;

import java.util.List;

/**
 * Created by stephan on 11/04/2017.
 */

public interface PhotoStore {
    boolean hasPhoto(long postId);

    void storePhotos(List<PhotoEntity> photos);

    PhotoEntity getRandomPhoto();
}
