package nl.acidcats.tumblrlikes.core.usecases.photos

import nl.acidcats.tumblrlikes.core.models.Photo
import rx.Observable

/**
 * Created on 03/10/2018.
 */
interface UpdatePhotoPropertyUseCase {
    fun updateLike(id: Long, isLiked: Boolean): Observable<Photo?>

    fun updateFavorite(id: Long, isFavorite: Boolean): Observable<Photo?>

    fun setHidden(id: Long): Observable<Photo?>
}
