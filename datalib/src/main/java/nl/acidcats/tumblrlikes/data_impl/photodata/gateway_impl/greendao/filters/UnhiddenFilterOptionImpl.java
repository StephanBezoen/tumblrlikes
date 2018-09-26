package nl.acidcats.tumblrlikes.data_impl.photodata.gateway_impl.greendao.filters;

import org.greenrobot.greendao.query.QueryBuilder;

import nl.acidcats.tumblrlikes.data_impl.photodata.gateway_impl.greendao.entities.PhotoEntity;
import nl.acidcats.tumblrlikes.db_impl_greendao.PhotoEntityDao;

/**
 * Created by stephan on 17/05/2017.
 */

public class UnhiddenFilterOptionImpl extends AbstractFilterOptionImpl {
    public UnhiddenFilterOptionImpl(PhotoEntityDao photoEntityDao) {
        setPhotoEntityDao(photoEntityDao);
    }

    @Override
    protected QueryBuilder<PhotoEntity> getQueryBuilder() {
        return _photoEntityDao.queryBuilder().where(PhotoEntityDao.Properties.IsHidden.eq(false));
    }
}
