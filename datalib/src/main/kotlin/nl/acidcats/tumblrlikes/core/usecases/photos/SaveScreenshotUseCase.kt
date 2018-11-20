package nl.acidcats.tumblrlikes.core.usecases.photos

import android.graphics.Bitmap
import rx.Observable

/**
 * Created on 20/11/2018.
 */
interface SaveScreenshotUseCase {
    fun saveScreenshot(screenshot: Bitmap, path: String, filename: String): Observable<Boolean>
}