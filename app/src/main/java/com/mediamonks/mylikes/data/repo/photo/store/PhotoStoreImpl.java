package com.mediamonks.mylikes.data.repo.photo.store;

import android.content.Context;
import android.util.Log;

import com.mediamonks.mylikes.BuildConfig;
import com.mediamonks.mylikes.data.vo.db.DaoMaster;
import com.mediamonks.mylikes.data.vo.db.PhotoEntity;
import com.mediamonks.mylikes.data.vo.db.PhotoEntityDao;

import java.util.List;

/**
 * Created by stephan on 11/04/2017.
 */

public class PhotoStoreImpl implements PhotoStore {
    private static final String TAG = PhotoStoreImpl.class.getSimpleName();

    private static final String DATABASE_NAME = "photos.db";

    private PhotoEntityDao _photoEntityDao;
    private final boolean _debug = BuildConfig.DEBUG;

    public PhotoStoreImpl(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DATABASE_NAME, null);
        _photoEntityDao = new DaoMaster(helper.getWritableDatabase()).newSession().getPhotoEntityDao();

//        _photoEntityDao.queryBuilder().buildDelete().executeDeleteWithoutDetachingEntities();
    }

    @Override
    public boolean hasPhoto(long postId) {
        List<PhotoEntity> photos = _photoEntityDao.queryBuilder().where(PhotoEntityDao.Properties.PhotoId.eq(postId)).list();
        return (photos != null) && (photos.size() > 0);
    }

    @Override
    public void storePhotos(List<PhotoEntity> photos) {
        if (_debug) Log.d(TAG, "storePhotos: " + photos.size());

        _photoEntityDao.saveInTx(photos);
    }

    @Override
    public long getPhotoCount() {
        return _photoEntityDao.count();
    }

    @Override
    public PhotoEntity getRandomPhoto() {
        long count = _photoEntityDao.queryBuilder().count();
        int index = (int)(count * Math.random());

        List<PhotoEntity> photos = _photoEntityDao.queryBuilder().limit(1).offset(index).list();
        if (photos != null && photos.size() == 1) {
            return photos.get(0);
        }

        return null;
    }
}
