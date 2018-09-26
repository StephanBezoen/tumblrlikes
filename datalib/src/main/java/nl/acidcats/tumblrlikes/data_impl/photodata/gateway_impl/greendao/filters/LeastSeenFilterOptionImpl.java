package nl.acidcats.tumblrlikes.data_impl.photodata.gateway_impl.greendao.filters;

import org.greenrobot.greendao.query.QueryBuilder;

import nl.acidcats.tumblrlikes.data_impl.photodata.gateway_impl.greendao.entities.PhotoEntity;
import nl.acidcats.tumblrlikes.db_impl_greendao.PhotoEntityDao;

/**
 * Created on 03/08/2017.
 */

public class LeastSeenFilterOptionImpl extends AbstractFilterOptionImpl {
    public LeastSeenFilterOptionImpl(PhotoEntityDao photoEntityDao) {
        setPhotoEntityDao(photoEntityDao);
    }

    @Override
    protected QueryBuilder<PhotoEntity> getQueryBuilder() {
        return _photoEntityDao.queryBuilder()
                .where(PhotoEntityDao.Properties.IsHidden.eq(false))
                .orderAsc(PhotoEntityDao.Properties.ViewCount);
    }
}
