package nl.acidcats.tumblrlikes.data.services

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.widget.Toast
import com.github.ajalt.timberkt.Timber
import nl.acidcats.tumblrlikes.BuildConfig
import nl.acidcats.tumblrlikes.LikesApplication
import nl.acidcats.tumblrlikes.R
import nl.acidcats.tumblrlikes.core.models.Photo
import nl.acidcats.tumblrlikes.core.repositories.PhotoDataRepository
import nl.acidcats.tumblrlikes.util.ServiceBinder
import nl.acidcats.tumblrlikes.util.security.SecurityHelper
import okhttp3.*
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.*
import javax.inject.Inject

/**
 * Created on 06/11/2018.
 */
class CacheService : Service() {

    @Inject
    lateinit var photoDataRepository: PhotoDataRepository
    @Inject
    lateinit var securityHelper: SecurityHelper

    private lateinit var serviceBinder: ServiceBinder<CacheService>
    private var isRunning = false
    private val debug = BuildConfig.DEBUG
    private var okHttpClient: OkHttpClient = OkHttpClient()
    private var photoCount: Int = 0
    private val handler = Handler()

    override fun onCreate() {
        super.onCreate()

        serviceBinder = ServiceBinder(this)

        (application as LikesApplication).appComponent.inject(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isRunning) {
            isRunning = true

            checkCachePhotos()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun checkCachePhotos() {
        if (photoDataRepository.hasUncachedPhotos()) {
            if (debug) Timber.d { "checkCachePhotos: more uncached photos" }

            Observable
                    .fromCallable { downloadPhoto(photoDataRepository.getNextUncachedPhoto()) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
        } else {
            if (debug) Timber.d { "checkCachePhotos: no more uncached photos" }

            isRunning = false

            stopSelf()
        }
    }

    private fun downloadPhoto(photo: Photo?) {
        val url = photo?.url ?: return

        val request = Request.Builder().url(url).build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Timber.e { "onFailure: ${e.message}" }

                onLoadError()
            }

            override fun onResponse(call: Call, response: Response) {
                if (debug) Timber.d { "onResponse: photo downloaded" }

                val filePath = savePhoto(response.body()?.byteStream(), photo)
                if (filePath != null) {
                    photoDataRepository.markAsCached(photo.id, filePath)

                    photoCount++
                    if (debug) Timber.d { "onResponse: $photoCount photos cached" }

                    checkCachePhotos()
                } else {
                    onLoadError()
                }
            }
        })
    }

    private fun savePhoto(inputStream: InputStream?, photo: Photo): String? {
        if (inputStream == null || photo.url == null) return null

        val url = photo.url!!
        val extension = url.substring(url.lastIndexOf("."))

        val filename = securityHelper.getHash(url) + extension
        val filePath = cacheDir.path + "/" + filename
        if (debug) Timber.d { "savePhoto: saving photo to $filePath" }

        try {
            val bufferedInputStream = BufferedInputStream(inputStream)
            val outputStream = BufferedOutputStream(FileOutputStream(filePath))

            val buffer = ByteArray(1024)

            do {
                val bytesRead = bufferedInputStream.read(buffer, 0, 1024)
                val hasMore = (bytesRead >= 0)

                if (hasMore) {
                    outputStream.write(buffer, 0, bytesRead)
                }
            } while (hasMore)

            outputStream.flush()
            outputStream.close()
            inputStream.close()
        } catch (e: IOException) {
            Timber.e { "savePhoto: ${e.message}" }

            onLoadError()
        }

        return filePath
    }

    private fun onLoadError() {
        handler.post {
            Toast.makeText(this, getString(R.string.error_load), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = serviceBinder
}