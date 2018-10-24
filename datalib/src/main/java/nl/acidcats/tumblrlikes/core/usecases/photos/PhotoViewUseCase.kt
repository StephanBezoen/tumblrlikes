package nl.acidcats.tumblrlikes.core.usecases.photos

import rx.Observable

/**
 * Created on 17/10/2018.
 */
interface PhotoViewUseCase {

    fun startPhotoView(id: Long, currentTime: Long): Observable<Boolean>

    fun endPhotoView(id: Long, currentTime: Long): Observable<Boolean>
}
