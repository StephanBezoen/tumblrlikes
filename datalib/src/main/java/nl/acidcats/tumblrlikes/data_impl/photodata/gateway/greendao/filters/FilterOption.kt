package nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.filters

import nl.acidcats.tumblrlikes.data_impl.photodata.gateway.greendao.entities.PhotoEntity

/**
 * Created by stephan on 17/05/2017.
 */

interface FilterOption {

    val count: Int

    fun getPhoto(index: Int): PhotoEntity
}
