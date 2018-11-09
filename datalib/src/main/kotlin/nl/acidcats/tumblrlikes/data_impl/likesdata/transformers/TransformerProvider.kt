package nl.acidcats.tumblrlikes.data_impl.likesdata.transformers

import nl.acidcats.tumblrlikes.data_impl.likesdata.models.TumblrLikeVO

/**
 * Created on 09/11/2018.
 */
class TransformerProvider {
    private val transformers: MutableList<Transformer> = ArrayList()

    init {
        transformers += TumblrLikePhotoTransformer()
        transformers += TumblrLikeTextTransformer()
        transformers += EmptyTransformer()
    }

    fun getTransformer(likeVO: TumblrLikeVO):Transformer = transformers.find { it.accepts(likeVO) }!!
}