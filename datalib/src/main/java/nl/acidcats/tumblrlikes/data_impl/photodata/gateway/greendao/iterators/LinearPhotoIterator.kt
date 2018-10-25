package nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.iterators

import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.filters.FilterOption

/**
 * Created on 25/10/2018.
 */
class LinearPhotoIterator constructor(filterOption: FilterOption) : AbstractPhotoIterator(filterOption) {

    private var currentIndex = 0

    override val nextIndex: Int
        get() {
            currentIndex = (currentIndex + 1) % totalCount

            return currentIndex
        }
}