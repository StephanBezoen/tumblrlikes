package nl.acidcats.tumblrlikes.data_impl.likesdata.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created on 24/10/2018.
 */
@JsonClass(generateAdapter = true)
data class TumblrLikesResponse(
        @Json(name = "liked_posts")
        val likes: List<TumblrLikeVO>
)