package nl.acidcats.tumblrlikes.data_impl.likesdata

import nl.acidcats.tumblrlikes.core.models.Photo
import nl.acidcats.tumblrlikes.data_impl.likesdata.models.TumblrLikeVO
import nl.acidcats.tumblrlikes.data_impl.likesdata.models.TumblrPhotoVO

/**
 * Created on 25/10/2018.
 */
class TumblrLikeTransformer {
    fun transformToPhotos(likeVO: TumblrLikeVO): List<Photo> {
        val photos = ArrayList<Photo>()

        val postVOs = likeVO.photos ?: return photos
        if (postVOs.isEmpty()) return photos


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
