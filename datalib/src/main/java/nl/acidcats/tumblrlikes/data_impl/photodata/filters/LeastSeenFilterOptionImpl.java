package nl.acidcats.tumblrlikes.data_impl.photodata.filters;

import org.greenrobot.greendao.query.QueryBuilder;

import nl.acidcats.tumblrlikes.data_impl.photodata.dbentity.PhotoEntity;
import nl.acidcats.tumblrlikes.data_impl.photodata.dbentity.PhotoEntityDao;

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
