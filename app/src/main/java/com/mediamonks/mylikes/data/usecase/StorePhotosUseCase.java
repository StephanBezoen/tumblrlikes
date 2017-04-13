package com.mediamonks.mylikes.data.usecase;

import com.mediamonks.mylikes.data.repo.photo.PhotoRepo;
import com.mediamonks.mylikes.data.vo.db.PhotoEntity;

import java.util.List;

/**
 * Created by stephan on 11/04/2017.
 */

public class StorePhotosUseCase {
    private PhotoRepo _repo;

    public StorePhotosUseCase(PhotoRepo repo) {
        _repo = repo;
    }

    public void storePhotos(List<PhotoEntity> photos) {
        _repo.storePhotos(photos);
    }
}
