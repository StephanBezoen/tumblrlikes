package nl.acidcats.tumblrlikes.core.models

/**
 * Created on 24/10/2018.
 */
data class Photo(
        val id: Long,
        val tumblrId: Long,
        val filePath: String? = null,
        val url: String? = null,
        val isFavorite: Boolean = false,
        val isLiked: Boolean = false,
        val isCached: Boolean = false,
        val viewCount: Int = 0,
        val viewTime: Long = 0,
        val timePerView: Long = 0
)
