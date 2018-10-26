package nl.acidcats.tumblrlikes.data_impl.likesdata.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created on 26/10/2018.
 */
@JsonClass(generateAdapter = true)
data class TumblrPageLinks(
        @Json(name = "next")
        val nextPage: TumblrPageLink?,

        @Json(name = "prev")
        val prevPage: TumblrPageLink?
)
