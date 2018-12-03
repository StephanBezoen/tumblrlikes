package nl.acidcats.tumblrlikes.core.usecases.photos

import nl.acidcats.tumblrlikes.core.models.Photo
import nl.acidcats.tumblrlikes.core.viewmodels.PhotoViewModel
import rx.Observable

/**
 * Created on 19/10/2018.
 */
interface GetFilteredPhotoUseCase {
    fun getNextFilteredPhoto(): Observable<PhotoViewModel>
}
