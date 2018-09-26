package nl.acidcats.tumblrlikes.data_impl.photodata.filters;

import org.greenrobot.greendao.query.QueryBuilder;

import nl.acidcats.tumblrlikes.data_impl.photodata.dbentity.PhotoEntity;
import nl.acidcats.tumblrlikes.data_impl.photodata.dbentity.PhotoEntityDao;

/**
 * Created by stephan on 17/05/2017.
 */

public class FavoriteFilterOptionImpl extends AbstractFilterOptionImpl {
    public FavoriteFilterOptionImpl(PhotoEntityDao photoEntityDao) {
        setPhotoEntityDao(photoEntityDao);
    }

    @Override
    protected QueryBuilder<PhotoEntity> getQueryBuilder() {
        return _photoEntityDao.queryBuilder().where(PhotoEntityDao.Properties.IsFavorite.eq(true));
    }
}
