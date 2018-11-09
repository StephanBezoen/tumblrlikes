package nl.acidcats.tumblrlikes.data_impl.likesdata.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created on 24/10/2018.
 */
@JsonClass(generateAdapter = true)
data class TumblrLikeVO(
        val type: String,
        val id: Long,
        @Json(name = "liked_timestamp")
        val timestamp: Long,
        val photos: List<TumblrPhotoPostVO>?,
        @Json(name = "body")
        val bodyText:String?
) {
    enum class TumblrPostType(val type: String) {
        PHOTO("photo"),
        VIDEO("video"),
        TEXT("text")
    }

    val isPhoto: Boolean
        get() = type == (TumblrPostType.PHOTO.type)

    val isText: Boolean
        get() = type == (TumblrPostType.TEXT.type)
}