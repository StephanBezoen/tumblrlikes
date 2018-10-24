package nl.acidcats.tumblrlikes.core.usecases.photos;

import android.os.Environment;
import android.util.Log;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.inject.Inject;

import nl.acidcats.tumblrlikes.core.models.Photo;
import nl.acidcats.tumblrlikes.core.repositories.PhotoDataRepository;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created on 23/10/2018.
 */
public class ExportPhotosUseCaseImpl implements ExportPhotosUseCase {
    private static final String TAG = ExportPhotosUseCaseImpl.class.getSimpleName();

    private PhotoDataRepository _photoDataRepository;
    private JsonAdapter<PhotoForExport> _jsonAdapter;

    @Inject
    public ExportPhotosUseCaseImpl(PhotoDataRepository photoDataRepository) {
        _photoDataRepository = photoDataRepository;

        _jsonAdapter = PhotoForExport.jsonAdapter(new Moshi.Builder().build());
    }

    @Override
    public Observable<Boolean> exportPhotos(String filename) {
        File pathFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File outputFile = new File(pathFile, filename);

        try {
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
            outputStream.write("[".getBytes());

            final int[] index = {0};

            return Observable
                    .fromCallable(() -> _photoDataRepository.getAllPhotos())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMapIterable(photos -> photos)
                    .flatMap(photo -> writeToStream(index[0]++, photo, outputStream))
                    .toList()
                    .map(photos -> {
                        Log.d(TAG, "exportPhotos: " + photos.size() + " photos written");

                        try {
                            outputStream.write("]".getBytes());
                            outputStream.close();
                        } catch (IOException e) {
                            Log.e(TAG, "exportPhotos: ");
                        }

                        return true;
                    });

        } catch (IOException exception) {
            return Observable.error(exception);
        }
    }

    private Observable<Photo> writeToStream(int index, Photo photo, OutputStream outputStream) {
        PhotoForExport photoForExport =
                PhotoForExport.create(photo.getUrl(), photo.isFavorite(), photo.isLiked(), photo.getViewCount(), photo.getViewTime());

        String json = _jsonAdapter.toJson(photoForExport);

        try {
            if (index > 0) {
                outputStream.write(",\r\n\t".getBytes());
            }
            outputStream.write(json.getBytes());
        } catch (IOException e) {
            return Observable.error(e);
        }

        return Observable.just(photo);
    }

    @AutoValue
    public static abstract class PhotoForExport {
        public abstract String url();

        public abstract int isFavorite();

        public abstract int isLiked();

        public abstract int viewCount();

        public abstract long viewTime();

        static PhotoForExport create(String url, boolean isFavorite, boolean isLiked, int viewCount, long viewTime) {
            return new AutoValue_ExportPhotosUseCaseImpl_PhotoForExport(url, isFavorite ? 1 : 0, isLiked ? 1 : 0, viewCount, viewTime);
        }

        public static JsonAdapter<PhotoForExport> jsonAdapter(Moshi moshi) {
            return new AutoValue_ExportPhotosUseCaseImpl_PhotoForExport.MoshiJsonAdapter(moshi);
        }
    }
}
