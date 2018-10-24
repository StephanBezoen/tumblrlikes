package nl.acidcats.tumblrlikes.data_impl.likesdata.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created on 24/10/2018.
 */
@JsonClass(generateAdapter = true)
data class TumblrMetaVO(
        val status: Int,
        @Json(name = "msg")
        val message: String
)
