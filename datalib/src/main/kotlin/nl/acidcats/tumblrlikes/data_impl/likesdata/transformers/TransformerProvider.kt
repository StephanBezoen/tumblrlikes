package nl.acidcats.tumblrlikes.data_impl.likesdata.transformers

import nl.acidcats.tumblrlikes.data_impl.likesdata.models.TumblrLikeVO

/**
 * Created on 09/11/2018.
 */
class TransformerProvider {
    private val transformers: MutableList<Transformer> = ArrayList()
    private val emptyTransformer = EmptyTransformer()

    init {
        transformers += TumblrLikePhotoTransformer()
        transformers += TumblrLikeTextTransformer()
    }

    fun getTransformer(likeVO: TumblrLikeVO):Transformer {
        val transformer = transformers.find { it.accepts(likeVO) }

        return transformer ?: emptyTransformer
    }
}