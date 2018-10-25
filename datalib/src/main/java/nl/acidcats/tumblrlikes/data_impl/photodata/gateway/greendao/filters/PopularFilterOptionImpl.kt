package nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.filters

import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.entities.PhotoEntity
import nl.acidcats.tumblrlikes.db_impl_greendao.PhotoEntityDao
import org.greenrobot.greendao.query.QueryBuilder

/**
 * Created on 25/10/2018.
 */
class PopularFilterOptionImpl constructor(photoEntityDao: PhotoEntityDao) : AbstractFilterOptionImpl() {

    override val queryBuilder: QueryBuilder<PhotoEntity> by lazy {
        val queryBuilder = photoEntityDao.queryBuilder()
        queryBuilder.where(
                queryBuilder.and(
                        PhotoEntityDao.Properties.IsHidden.eq(false),
                        queryBuilder.or(
                                PhotoEntityDao.Properties.IsFavorite.eq(true),
                                PhotoEntityDao.Properties.IsLiked.eq(true)
                        )
                )
        )
    }
}