package nl.acidcats.tumblrlikes.core.usecases.photos

import nl.acidcats.tumblrlikes.core.models.Photo
import nl.acidcats.tumblrlikes.core.viewmodels.PhotoViewModel
import rx.Observable

/**
 * Created on 03/10/2018.
 */
interface UpdatePhotoPropertyUseCase {
    fun updateLike(id: Long, isLiked: Boolean): Observable<PhotoViewModel>

    fun updateFavorite(id: Long, isFavorite: Boolean): Observable<PhotoViewModel>

    fun setHidden(id: Long): Observable<PhotoViewModel>
}
