package nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.filters;

import org.greenrobot.greendao.query.QueryBuilder;

import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.entities.PhotoEntity;
import nl.acidcats.tumblrlikes.db_impl_greendao.PhotoEntityDao;

/**
 * Created by stephan on 17/05/2017.
 */

public class FavoriteFilterOptionImpl extends AbstractFilterOptionImpl {
    public FavoriteFilterOptionImpl(PhotoEntityDao photoEntityDao) {
        setPhotoEntityDao(photoEntityDao);
    }

    @Override
    protected QueryBuilder<PhotoEntity> getQueryBuilder() {
        QueryBuilder<PhotoEntity> queryBuilder = _photoEntityDao.queryBuilder();
        return queryBuilder.where(
                queryBuilder.and(
                        PhotoEntityDao.Properties.IsFavorite.eq(true),
                        PhotoEntityDao.Properties.IsHidden.eq(false)
                )
        );
    }
}
