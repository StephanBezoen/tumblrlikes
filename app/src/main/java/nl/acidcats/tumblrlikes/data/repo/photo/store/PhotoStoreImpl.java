package nl.acidcats.tumblrlikes.data.repo.photo.store;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import org.greenrobot.greendao.query.CountQuery;
import org.greenrobot.greendao.query.Query;

import java.util.List;

import nl.acidcats.tumblrlikes.BuildConfig;
import nl.acidcats.tumblrlikes.data.vo.db.DaoMaster;
import nl.acidcats.tumblrlikes.data.vo.db.PhotoEntity;
import nl.acidcats.tumblrlikes.data.vo.db.PhotoEntityDao;
import nl.acidcats.tumblrlikes.util.ListUtil;
import nl.acidcats.tumblrlikes.util.database.DbOpenHelper;

/**
 * Created by stephan on 11/04/2017.
 */

public class PhotoStoreImpl implements PhotoStore {
    private static final String TAG = PhotoStoreImpl.class.getSimpleName();

    private static final String DATABASE_NAME = "photos.db";

    private final CountQuery<PhotoEntity> _countQuery;
    private final Query<PhotoEntity> _uncachedQuery;
    private final PhotoEntityDao _photoEntityDao;
    private final boolean _debug = BuildConfig.DEBUG;

    public PhotoStoreImpl(Context context) {
        DaoMaster.OpenHelper helper = new DbOpenHelper(context, DATABASE_NAME, null);
        _photoEntityDao = new DaoMaster(helper.getWritableDatabase()).newSession().getPhotoEntityDao();

        _countQuery = _photoEntityDao.queryBuilder().buildCount();
        _uncachedQuery = _photoEntityDao.queryBuilder().where(PhotoEntityDao.Properties.IsCached.eq(false)).limit(1).build();
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
        return _countQuery.count();
    }

    @Override
    @Nullable
    public PhotoEntity getRandomPhoto() {
        if (_debug) Log.d(TAG, "getRandomPhoto: ");

        int index = (int) (getPhotoCount() * Math.random());
        if (_debug) Log.d(TAG, "getRandomPhoto: index = " + index);

        PhotoEntity photo = ListUtil.getFirstFromList(_photoEntityDao.queryBuilder().limit(1).offset(index).list());
        if (photo == null) return null;

        // increase view count
        photo.setViewCount(photo.getViewCount() + 1);
        if (_debug) Log.d(TAG, "getRandomPhoto: view count now " + photo.getViewCount());
        storePhoto(photo);

        return photo;
    }

    @Override
    public boolean hasUncachedPhotos() {
        return getNextUncachedPhoto() != null;
    }

    @Override
    @Nullable
    public PhotoEntity getNextUncachedPhoto() {
        return ListUtil.getFirstFromList(_uncachedQuery.forCurrentThread().list());
    }

    @Override
    public void storePhoto(PhotoEntity photo) {
        _photoEntityDao.save(photo);
    }

    @Override
    public void addViewTime(PhotoEntity photo, long timeInMs) {
        photo.setViewTime(photo.getViewTime() + timeInMs);
        if (_debug) Log.d(TAG, "addViewTime: view time now " + (timeInMs / 1000) + " ms");

        storePhoto(photo);
    }

    @Override
    @Nullable
    public PhotoEntity getPhotoByUrl(String url) {
        return ListUtil.getFirstFromList(_photoEntityDao.queryBuilder().where(PhotoEntityDao.Properties.Url.eq(url)).list());
    }
}
