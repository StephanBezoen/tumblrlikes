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
    val afterSeconds:Long
        get() {
            return after?.toLong() ?: 0
        }
    val beforeSeconds:Long
        get() {
            return before?.toLong() ?: 0
        }
}
