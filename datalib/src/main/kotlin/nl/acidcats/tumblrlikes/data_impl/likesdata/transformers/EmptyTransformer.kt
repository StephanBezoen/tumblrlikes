package nl.acidcats.tumblrlikes.data_impl.likesdata.transformers

import nl.acidcats.tumblrlikes.core.models.Photo
import nl.acidcats.tumblrlikes.data_impl.likesdata.models.TumblrLikeVO

/**
 * Created on 09/11/2018.
 */
class EmptyTransformer : Transformer {
    override fun accepts(like: TumblrLikeVO): Boolean {
        return true
    }

    override fun transform(like: TumblrLikeVO): List<Photo> {
        return ArrayList()
    }
}