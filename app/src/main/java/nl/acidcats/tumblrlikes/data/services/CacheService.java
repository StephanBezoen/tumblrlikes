package nl.acidcats.tumblrlikes.data.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import nl.acidcats.tumblrlikes.BuildConfig;
import nl.acidcats.tumblrlikes.LikesApplication;
import nl.acidcats.tumblrlikes.data.repo.photo.PhotoRepo;
import nl.acidcats.tumblrlikes.data.vo.db.PhotoEntity;
import nl.acidcats.tumblrlikes.util.security.SecurityHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by stephan on 18/04/2017.
 */

public class CacheService extends Service {
    private static final String TAG = CacheService.class.getSimpleName();

    @Inject
    PhotoRepo _photoRepo;
    @Inject
    SecurityHelper _securityHelper;

    private ServiceBinder _serviceBinder;
    private boolean _isRunning;
    private final boolean _debug = BuildConfig.DEBUG;
    private OkHttpClient _client;

    @Override
    public void onCreate() {
        super.onCreate();

        if (_debug) Log.d(TAG, "onCreate: ");

        _serviceBinder = new ServiceBinder<>(this);

        _client = new OkHttpClient();

        ((LikesApplication) getApplication()).getMyComponent().inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (_debug) Log.d(TAG, "onStartCommand: _isRunning = " + _isRunning);

        if (!_isRunning) {
            _isRunning = true;

            checkCachePhotos();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void checkCachePhotos() {
        if (_photoRepo.hasUncachedPhotos()) {
            if (_debug) Log.d(TAG, "checkCachePhotos: more uncached photos");

            Observable.fromCallable(() -> downloadPhoto(_photoRepo.getNextUncachedPhoto()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
        } else {
            if (_debug) Log.d(TAG, "checkCachePhotos: no more uncached photos");

            _isRunning = false;

            stopSelf();
        }
    }

    private Void downloadPhoto(PhotoEntity photo) {
        if (_debug) Log.d(TAG, "downloadPhoto: " + photo);

        Request request = new Request.Builder()
                .url(photo.getUrl())
                .build();

        _client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (_debug) Log.d(TAG, "onResponse: ");

                String filepath = savePhoto(response.body().byteStream(), photo);

                if (filepath != null) {
                    _photoRepo.markAsCached(photo, filepath);

                    checkCachePhotos();
                }
            }
        });

        return null;
    }

    private String savePhoto(InputStream inputStream, PhotoEntity photo) {
        String url = photo.getUrl();
        String extension = url.substring(url.lastIndexOf("."));

        String filename = _securityHelper.getHash(url) + extension;
        String filePath = getCacheDir().getPath() + "/" + filename;
        if (_debug) Log.d(TAG, "savePhoto: saving photo to " + filePath);

        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        try {
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(filePath));

            int read;
            byte[] buffer = new byte[1024];
            while ((read = bufferedInputStream.read(buffer, 0, 1024)) >= 0) {
                outputStream.write(buffer, 0, read);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "savePhoto: " + e.getMessage());

            return null;
        }

        if (_debug) Log.d(TAG, "savePhoto: photo saved");

        return filePath;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return _serviceBinder;
    }
}
