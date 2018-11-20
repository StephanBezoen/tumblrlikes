package nl.acidcats.tumblrlikes.core.usecases.photos

import android.graphics.Bitmap
import com.github.ajalt.timberkt.Timber
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

/**
 * Created on 20/11/2018.
 */
class SaveScreenshotUseCaseImpl @Inject constructor() : SaveScreenshotUseCase {
    override fun saveScreenshot(screenshot: Bitmap, path: String, filename: String): Observable<Boolean> {
        return Observable
                .fromCallable {
                    val dirFile = File(path)
                    if (!dirFile.exists()) {
                        dirFile.mkdir()
                    }

                    val outputStream = BufferedOutputStream(FileOutputStream(File(dirFile.path, filename)))
                    try {
                        screenshot.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)

                        true
                    } catch (e: Exception) {
                        Timber.e { "saveScreenshot: " + e.message }

                        false
                    } finally {
                        outputStream.close()
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}