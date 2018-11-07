package nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.filters

import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.entities.PhotoEntity
import org.greenrobot.greendao.query.CountQuery
import org.greenrobot.greendao.query.QueryBuilder

/**
 * Created on 25/10/2018.
 */
abstract class AbstractFilterOptionImpl : FilterOption {

    override val count
        get() = queryBuilder.count().toInt()

    override fun getPhoto(index: Int): PhotoEntity = queryBuilder.limit(1).offset(index).build().forCurrentThread().unique()

    protected abstract val queryBuilder: QueryBuilder<PhotoEntity>
}
