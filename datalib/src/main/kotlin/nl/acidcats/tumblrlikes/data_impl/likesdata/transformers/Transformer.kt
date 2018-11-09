package nl.acidcats.tumblrlikes.data_impl.likesdata.transformers

import nl.acidcats.tumblrlikes.core.models.Photo
import nl.acidcats.tumblrlikes.data_impl.likesdata.models.TumblrLikeVO

/**
 * Created on 09/11/2018.
 */
interface Transformer {
    fun accepts(like: TumblrLikeVO): Boolean

    fun transform(like: TumblrLikeVO): List<Photo>
}