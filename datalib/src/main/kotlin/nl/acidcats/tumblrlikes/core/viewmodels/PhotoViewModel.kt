package nl.acidcats.tumblrlikes.core.viewmodels

import nl.acidcats.tumblrlikes.core.models.Photo

/**
 * Created on 03/12/2018.
 */
sealed class PhotoViewModel {
    companion object {
        fun from(photo: Photo?): PhotoViewModel {
            if (photo == null || photo.url == null) return InvalidPhoto

            var url = if (photo.isCached) photo.filePath else photo.url
            if (url.isNullOrEmpty()) return InvalidPhoto

            if (!url.startsWith("http")) url = "file:$url"

            return ValidPhotoViewModel(
                    photoId = photo.id,
                    url = url, fallbackUrl = photo.url,
                    isFavorite = photo.isFavorite, isLiked = photo.isLiked, viewCount = photo.viewCount)
        }
    }
}

data class ValidPhotoViewModel(
        val photoId: Long,
        val url: String,
        val fallbackUrl: String,
        val isFavorite: Boolean,
        val isLiked: Boolean,
        val viewCount: Int
) : PhotoViewModel()


object InvalidPhoto : PhotoViewModel()
