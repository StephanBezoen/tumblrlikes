package nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.filters;

import org.greenrobot.greendao.query.QueryBuilder;

import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.entities.PhotoEntity;
import nl.acidcats.tumblrlikes.db_impl_greendao.PhotoEntityDao;

/**
 * Created on 10/10/2018.
 */
public class MostTimePerViewFilterOptionImpl extends AbstractFilterOptionImpl {

    public MostTimePerViewFilterOptionImpl(PhotoEntityDao photoEntityDao) {
        setPhotoEntityDao(photoEntityDao);
    }

    @Override
    protected QueryBuilder<PhotoEntity> getQueryBuilder() {
        return _photoEntityDao.queryBuilder()
                .where(PhotoEntityDao.Properties.IsHidden.eq(false))
                .orderDesc(PhotoEntityDao.Properties.TimePerView);
    }
}
