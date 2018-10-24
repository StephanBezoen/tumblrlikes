package nl.acidcats.tumblrlikes.core.usecases.photos

import rx.Observable

/**
 * Created on 23/10/2018.
 */
interface ExportPhotosUseCase {
    fun exportPhotos(path:String, filename: String): Observable<Boolean>
}
