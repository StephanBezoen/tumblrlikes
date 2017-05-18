package nl.acidcats.tumblrlikes.data.repo.photo.store.filters;

import android.support.annotation.Nullable;

import org.greenrobot.greendao.query.CountQuery;
import org.greenrobot.greendao.query.QueryBuilder;

import nl.acidcats.tumblrlikes.data.vo.db.PhotoEntity;
import nl.acidcats.tumblrlikes.data.vo.db.PhotoEntityDao;

/**
 * Created by stephan on 17/05/2017.
 */

public abstract class AbstractFilterOptionImpl implements FilterOption {
    private static final String TAG = AbstractFilterOptionImpl.class.getSimpleName();

    protected PhotoEntityDao _photoEntityDao;
    protected CountQuery<PhotoEntity> _countQuery;

    protected void setPhotoEntityDao(PhotoEntityDao photoEntityDao) {
        _photoEntityDao = photoEntityDao;

        _countQuery = getQueryBuilder().buildCount();
    }

    @Override
    public long getCount() {
        return _countQuery.count();
    }

    @Nullable
    @Override
    public PhotoEntity getPhoto(int index) {
        return getQueryBuilder().limit(1).offset(index).unique();
    }

    protected abstract QueryBuilder<PhotoEntity> getQueryBuilder();
}
