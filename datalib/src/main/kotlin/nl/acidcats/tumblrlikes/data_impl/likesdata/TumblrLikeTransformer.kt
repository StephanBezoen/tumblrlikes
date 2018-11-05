package nl.acidcats.tumblrlikes.data_impl.likesdata

import nl.acidcats.tumblrlikes.core.models.Photo
import nl.acidcats.tumblrlikes.data_impl.likesdata.models.TumblrLikeVO
import nl.acidcats.tumblrlikes.data_impl.likesdata.models.TumblrPhotoVO

/**
 * Created on 25/10/2018.
 */
class TumblrLikeTransformer {
    fun transformToPhotos(likeVO: TumblrLikeVO): List<Photo>? {
        val postVOs = likeVO.photos ?: return null
        if (postVOs.isEmpty()) return null

        val photos = ArrayList<Photo>()

        for (postVO in postVOs) {
            val tumblrPhotoVOs = ArrayList<TumblrPhotoVO>()

            postVO.originalPhoto?.let { tumblrPhotoVOs.add(it) }

            postVO.altPhotos?.let { tumblrPhotoVOs.addAll(it) }

            if (!tumblrPhotoVOs.isEmpty()) {
                tumblrPhotoVOs.sortByDescending { it.size }

                photos.add(Photo(tumblrId = likeVO.id, url = tumblrPhotoVOs[0].url))
            }
        }

        return photos
    }
}
