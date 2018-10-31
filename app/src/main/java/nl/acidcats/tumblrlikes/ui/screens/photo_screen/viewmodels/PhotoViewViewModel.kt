package nl.acidcats.tumblrlikes.ui.screens.photo_screen.viewmodels

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created on 31/10/2018.
 */
@Parcelize
data class PhotoViewViewModel(
        val photoId: Long,
        val url: String?,
        val fallbackUrl: String?,
        val isFavorite: Boolean,
        val isLiked: Boolean,
        val viewCount: Int
) : Parcelable {
    companion object {
        fun isValid(viewModel: PhotoViewViewModel?): Boolean {
            return viewModel?.url != null && !viewModel.url.isEmpty()
        }
    }
}