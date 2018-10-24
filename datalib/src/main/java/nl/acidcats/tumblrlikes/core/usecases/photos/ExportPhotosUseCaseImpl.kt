package nl.acidcats.tumblrlikes.core.usecases.photos

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import nl.acidcats.tumblrlikes.core.repositories.PhotoDataRepository
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File
import javax.inject.Inject

/**
 * Created on 24/10/2018.
 */
class ExportPhotosUseCaseImpl @Inject constructor(private val photoDataRepository: PhotoDataRepository) : ExportPhotosUseCase {

    private val jsonAdapter: PhotoForExportJsonAdapter = PhotoForExportJsonAdapter(Moshi.Builder().build())

    override fun exportPhotos(path: String, filename: String): Observable<Boolean> {

        return Observable
                .fromCallable { photoDataRepository.getAllPhotos() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { photos ->
                    photos.joinToString(prefix = "[\r\n\t", postfix = "\r\n]", separator = ",\r\n\t",
                            transform = {
                                jsonAdapter.toJson(PhotoForExport(it.url, if (it.isFavorite) 1 else 0, if (it.isLiked) 1 else 0, it.viewCount, it.viewTime))
                            })
                }
                .map {
                    File(path, filename).writeText(it)

                    true
                }
    }
}

/**
 * Created on 24/10/2018.
 */
@JsonClass(generateAdapter = true)
data class PhotoForExport (
        val url:String?,
        val isFavorite:Int,
        val isLiked:Int,
        val viewCount:Int,
        val viewTime:Long
)