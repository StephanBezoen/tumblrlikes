package nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.filters;

import android.support.annotation.Nullable;

import org.greenrobot.greendao.query.CountQuery;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.entities.PhotoEntity;
import nl.acidcats.tumblrlikes.db_impl_greendao.PhotoEntityDao;

/**
 * Created by stephan on 17/05/2017.
 */

abstract class AbstractFilterOptionImpl implements FilterOption {
    private static final String TAG = AbstractFilterOptionImpl.class.getSimpleName();

    PhotoEntityDao _photoEntityDao;

    private CountQuery<PhotoEntity> _countQuery;

    void setPhotoEntityDao(PhotoEntityDao photoEntityDao) {
        _photoEntityDao = photoEntityDao;

        _countQuery = getQueryBuilder().buildCount();
    }

    @Override
    public int getCount() {
        return (int)_countQuery.count();
    }

    @Nullable
    @Override
    public PhotoEntity getPhoto(int index) {
        return getQueryBuilder().limit(1).offset(index).unique();
    }

    protected abstract QueryBuilder<PhotoEntity> getQueryBuilder();
}
