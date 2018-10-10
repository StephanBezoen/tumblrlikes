package nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.filters;

import org.greenrobot.greendao.query.QueryBuilder;

import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.entities.PhotoEntity;
import nl.acidcats.tumblrlikes.db_impl_greendao.PhotoEntityDao;

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
        return queryBuilder.where(
                queryBuilder.and(
                        PhotoEntityDao.Properties.IsHidden.eq(false),
                        queryBuilder.or(
                                PhotoEntityDao.Properties.IsFavorite.eq(true),
                                PhotoEntityDao.Properties.LikeCount.gt(0)
                        )
                )
        );
    }
}
