package nl.acidcats.tumblrlikes.data_impl.likesdata.models

import com.squareup.moshi.JsonClass

/**
 * Created on 26/10/2018.
 */
@JsonClass(generateAdapter = true)
data class TumblrQueryParams (
        val after:String?,
        val before:String?
) {
    val afterInMs:Long
        get() {
            val ms = after?.toLong() ?: 0
            return (1000L * ms)
        }
    val beforeInMs:Long
        get() {
            val ms = before?.toLong() ?: 0
            return (1000L * ms)
        }
}
