package nl.acidcats.tumblrlikes.data_impl.likesdata.models

import com.squareup.moshi.JsonClass

/**
 * Created on 24/10/2018.
 */
@JsonClass(generateAdapter = true)
data class TumblrPhotoVO(
        val url: String,
        val width: Int,
        val height: Int
) {
    val size: Long
        get() = (width.toLong() * height.toLong())
}