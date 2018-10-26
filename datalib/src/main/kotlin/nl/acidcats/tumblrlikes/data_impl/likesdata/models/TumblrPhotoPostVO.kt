package nl.acidcats.tumblrlikes.data_impl.likesdata.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created on 24/10/2018.
 */
@JsonClass(generateAdapter = true)
data class TumblrPhotoPostVO (
        @Json(name = "original_size")
        val originalPhoto:TumblrPhotoVO?,
        @Json(name = "alt_sizes")
        val altPhotos:List<TumblrPhotoVO>?
)