package nl.acidcats.tumblrlikes.data_impl.photodata.filters;

import org.greenrobot.greendao.query.QueryBuilder;

import nl.acidcats.tumblrlikes.data_impl.photodata.dbentity.PhotoEntity;
import nl.acidcats.tumblrlikes.data_impl.photodata.dbentity.PhotoEntityDao;

/**
 * Created by stephan on 19/05/2017.
 */

public class LatestFilterOptionImpl extends AbstractFilterOptionImpl {
    public LatestFilterOptionImpl(PhotoEntityDao photoEntityDao) {
        setPhotoEntityDao(photoEntityDao);
    }

    @Override
    protected QueryBuilder<PhotoEntity> getQueryBuilder() {
        return _photoEntityDao.queryBuilder()
                .where(PhotoEntityDao.Properties.IsHidden.eq(false))
                .orderDesc(PhotoEntityDao.Properties.Id);
    }
}
