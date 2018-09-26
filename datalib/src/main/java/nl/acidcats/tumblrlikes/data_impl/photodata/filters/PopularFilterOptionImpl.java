package nl.acidcats.tumblrlikes.data_impl.photodata.filters;

import org.greenrobot.greendao.query.QueryBuilder;

import nl.acidcats.tumblrlikes.data_impl.photodata.dbentity.PhotoEntity;
import nl.acidcats.tumblrlikes.data_impl.photodata.dbentity.PhotoEntityDao;

/**
 * Created by stephan on 17/05/2017.
 */

public class PopularFilterOptionImpl extends AbstractFilterOptionImpl {
    public PopularFilterOptionImpl(PhotoEntityDao photoEntityDao) {
        setPhotoEntityDao(photoEntityDao);
    }

    @Override
    protected QueryBuilder<PhotoEntity> getQueryBuilder() {
        QueryBuilder<PhotoEntity> queryBuilder = _photoEntityDao.queryBuilder();
        return queryBuilder.where(queryBuilder.or(PhotoEntityDao.Properties.IsFavorite.eq(true), PhotoEntityDao.Properties.LikeCount.gt(0)));
    }
}
