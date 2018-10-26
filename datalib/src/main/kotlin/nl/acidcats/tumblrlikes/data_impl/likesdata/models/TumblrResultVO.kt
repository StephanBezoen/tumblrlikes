package nl.acidcats.tumblrlikes.data_impl.likesdata.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created on 24/10/2018.
 */
@JsonClass(generateAdapter = true)
data class TumblrResultVO<T>(
        @Json(name = "meta")
        val metaData: TumblrMetaVO,
        @Json(name = "response")
        val response: T
)
