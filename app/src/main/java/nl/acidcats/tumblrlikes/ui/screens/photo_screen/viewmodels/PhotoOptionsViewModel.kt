package nl.acidcats.tumblrlikes.ui.screens.photo_screen.viewmodels

/**
 * Created on 30/10/2018.
 */
data class PhotoOptionsViewModel (
        val photoId:Long,
        val isPhotoFavorite:Boolean,
        val isPhotoLiked:Boolean,
        val viewCount:Int

)