package nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.iterators

import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.filters.FilterOption

/**
 * Created on 25/10/2018.
 */
class RandomPhotoIterator constructor(filterOption: FilterOption) : AbstractPhotoIterator(filterOption) {
    override val nextIndex: Int
        get() = Math.floor(totalCount * Math.random()).toInt()
}