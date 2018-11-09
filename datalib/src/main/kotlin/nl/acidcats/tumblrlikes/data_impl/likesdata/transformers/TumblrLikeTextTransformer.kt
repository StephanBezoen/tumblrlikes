package nl.acidcats.tumblrlikes.data_impl.likesdata.transformers

import nl.acidcats.tumblrlikes.core.models.Photo
import nl.acidcats.tumblrlikes.data_impl.likesdata.models.TumblrLikeVO

/**
 * Created on 09/11/2018.
 */
class TumblrLikeTextTransformer : Transformer {
    private val regex = Regex("<img src=\"([a-zA-Z0-9.:/_]*)")

    override fun accepts(like: TumblrLikeVO): Boolean {
        if (!like.isText) return false

        if (like.bodyText == null) return false

        val result = regex.find(like.bodyText) ?: return false

        val groupValues = result.groupValues
        if (groupValues.size < 2) return false

        return true
    }

    override fun transform(like: TumblrLikeVO): List<Photo> {
        val imageUrl = regex.find(like.bodyText!!)!!.groupValues[1]
        val photo = Photo(tumblrId = like.id, url = imageUrl)

        return listOf(photo)
    }
}