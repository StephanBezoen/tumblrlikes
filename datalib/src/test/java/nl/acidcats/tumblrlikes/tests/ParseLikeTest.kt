package nl.acidcats.tumblrlikes.tests

import com.squareup.moshi.Moshi
import nl.acidcats.tumblrlikes.data_impl.likesdata.models.TumblrLikeVO
import nl.acidcats.tumblrlikes.data_impl.likesdata.models.TumblrLikeVOJsonAdapter
import nl.acidcats.tumblrlikes.data_impl.likesdata.transformers.EmptyTransformer
import nl.acidcats.tumblrlikes.data_impl.likesdata.transformers.TransformerProvider
import nl.acidcats.tumblrlikes.data_impl.likesdata.transformers.TumblrLikePhotoTransformer
import nl.acidcats.tumblrlikes.data_impl.likesdata.transformers.TumblrLikeTextTransformer
import org.junit.Assert.*
import org.junit.Test

/**
 * Created on 09/11/2018.
 */
class ParseLikeTest {


    @Test
    fun testParseTextJson() {
        val tumblrLike = getTumblrLikeVO(TumblrLikeJsonMock.TEXT_LIKE)

        assertNotNull(tumblrLike)
        assertEquals(tumblrLike!!.id, 179516097297L)
        assertNotNull(tumblrLike.bodyText)

        val transformer = TransformerProvider().getTransformer(tumblrLike)

        assertNotNull(transformer)
        assertTrue(transformer is TumblrLikeTextTransformer)
        assertTrue(transformer!!.accepts(tumblrLike))

        val photos = transformer.transform(tumblrLike)
        assertEquals(photos.size, 1)

        val photo = photos[0]
        assertNotNull(photo.url)
        assertEquals(photo.url, "https://66.media.tumblr.com/32b744bef025114c8b51bb3d5e5f078f/tumblr_phb7tsO4Dj1uj0hhs_1280.jpg")
    }

    @Test
    fun testRegex() {
        val url = "<img src=\"https://66.media.tumblr.com/32b744bef025114c8b51bb3d5e5f078f/tumblr_phb7tsO4Dj1uj0hhs_1280.jpg\""

        val regex = Regex("<img src=\"([a-zA-Z0-9.:/_]*)")
        val result = regex.find(url)

        val imageUrl = result!!.groupValues[1]
        assertEquals(imageUrl, "https://66.media.tumblr.com/32b744bef025114c8b51bb3d5e5f078f/tumblr_phb7tsO4Dj1uj0hhs_1280.jpg")
    }

    private fun getTumblrLikeVO(json: String): TumblrLikeVO? {
        return TumblrLikeVOJsonAdapter(Moshi.Builder().build()).fromJson(json)
    }

    @Test
    fun testParsePhotoJson() {
        val tumblrLike = getTumblrLikeVO(TumblrLikeJsonMock.PHOTO_LIKE)

        assertNotNull(tumblrLike)
        assertEquals(tumblrLike!!.id, 179516206403L)

        val transformer = TransformerProvider().getTransformer(tumblrLike)
        assertNotNull(transformer)
        assertTrue(transformer is TumblrLikePhotoTransformer)
        assertTrue(transformer!!.accepts(tumblrLike))

        val photos = transformer.transform(tumblrLike)
        assertEquals(photos.size, 1)

        val photo = photos[0]
        assertNotNull(photo.url)
        assertEquals(photo.url, "https://66.media.tumblr.com/30e407c70cd625e02af04e49b8c96ed5/tumblr_p928p09OP11qjx7gho1_500.jpg")
    }

    @Test
    fun testParseVideoJson() {
        val tumblrLike = getTumblrLikeVO(TumblrLikeJsonMock.VIDEO_LIKE)

        assertNotNull(tumblrLike)
        assertEquals(tumblrLike!!.id, 179521270301L)
        val transformer = TransformerProvider().getTransformer(tumblrLike)
        assertNotNull(transformer)
        assertTrue(transformer is EmptyTransformer)
        assertTrue(transformer!!.accepts(tumblrLike))

        val photos = transformer.transform(tumblrLike)
        assertEquals(photos.size, 0)
    }
}