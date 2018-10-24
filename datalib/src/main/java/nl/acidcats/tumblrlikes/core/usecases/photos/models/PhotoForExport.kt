package nl.acidcats.tumblrlikes.core.usecases.photos.models

import com.squareup.moshi.JsonClass

/**
 * Created on 24/10/2018.
 */
@JsonClass(generateAdapter = true)
data class PhotoForExport (
        val url:String?,
        val isFavorite:Int,
        val isLiked:Int,
        val viewCount:Int,
        val viewTime:Long
)