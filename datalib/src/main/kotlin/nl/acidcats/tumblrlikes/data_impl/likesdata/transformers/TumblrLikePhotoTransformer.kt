package nl.acidcats.tumblrlikes.data_impl.likesdata.transformers

import nl.acidcats.tumblrlikes.core.models.Photo
import nl.acidcats.tumblrlikes.data_impl.likesdata.models.TumblrLikeVO
import nl.acidcats.tumblrlikes.data_impl.likesdata.models.TumblrPhotoVO

/**
 * Created on 09/11/2018.
 */
class TumblrLikePhotoTransformer : Transformer {

    override fun accepts(like: TumblrLikeVO): Boolean {
        return like.isPhoto
    }

    override fun transform(like: TumblrLikeVO): List<Photo> {
        val photos = ArrayList<Photo>()

        val postVOs = like.photos ?: return photos
        if (postVOs.isEmpty()) return photos


        for (postVO in postVOs) {
            val tumblrPhotoVOs = ArrayList<TumblrPhotoVO>()

            postVO.originalPhoto?.let { tumblrPhotoVOs.add(it) }

            postVO.altPhotos?.let { tumblrPhotoVOs.addAll(it) }

            if (!tumblrPhotoVOs.isEmpty()) {
                tumblrPhotoVOs.sortByDescending { it.size }

                photos.add(Photo(tumblrId = like.id, url = tumblrPhotoVOs[0].url))
            }
        }

        return photos
    }
}