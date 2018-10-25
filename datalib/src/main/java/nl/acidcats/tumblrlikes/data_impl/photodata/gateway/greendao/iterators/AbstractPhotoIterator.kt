package nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.iterators

import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.entities.PhotoEntity
import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.filters.FilterOption

/**
 * Created on 25/10/2018.
 */
abstract class AbstractPhotoIterator internal constructor(private val filterOption: FilterOption) : Iterator<PhotoEntity> {

    protected val totalCount by lazy { filterOption.count }

    protected abstract val nextIndex: Int

    override fun hasNext() = true

    override fun next(): PhotoEntity = filterOption.getPhoto(nextIndex)
}