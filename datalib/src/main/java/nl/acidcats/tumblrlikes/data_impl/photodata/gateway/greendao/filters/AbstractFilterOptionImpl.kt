package nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.filters

import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.entities.PhotoEntity
import org.greenrobot.greendao.query.CountQuery
import org.greenrobot.greendao.query.QueryBuilder

/**
 * Created on 25/10/2018.
 */
abstract class AbstractFilterOptionImpl : FilterOption {

    private val countQuery: CountQuery<PhotoEntity> by lazy { queryBuilder.buildCount() }

    override val count
        get() = countQuery.count().toInt()

    override fun getPhoto(index: Int): PhotoEntity = queryBuilder.limit(1).offset(index).unique()

    protected abstract val queryBuilder: QueryBuilder<PhotoEntity>
}
