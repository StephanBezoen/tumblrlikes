package nl.acidcats.tumblrlikes.data_impl.likesdata.gateway;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import nl.acidcats.tumblrlikes.data_impl.likesdata.models.TumblrLikeVO;
import nl.acidcats.tumblrlikes.data_impl.likesdata.models.TumblrLikesResponse;
import nl.acidcats.tumblrlikes.data_impl.likesdata.models.TumblrResultVO;
import nl.acidcats.tumblrlikes.datalib.BuildConfig;
import nl.acidcats.tumblrlikes.datalib.R;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by stephan on 28/03/2017.
 */

public class NetLikesDataGateway implements LikesDataGateway {
    private static final String TAG = NetLikesDataGateway.class.getSimpleName();

    private TumblrApi _tumblrApi;
    private String _apiKey;

    public NetLikesDataGateway(Context context) {
        initRestApi(context);

        _apiKey = BuildConfig.CONSUMER_KEY;
    }

    @Override
    public Observable<List<TumblrLikeVO>> getLikes(String blogName, int count, long beforeTime) {
        return _tumblrApi
                .getLikes(blogName, _apiKey, count, beforeTime)
                .subscribeOn(Schedulers.io())
                .map(result -> result.getResponse().getLikes());
    }

    interface TumblrApi {
        @GET("{blogName}/likes")
        Observable<TumblrResultVO<TumblrLikesResponse>> getLikes(
                @Path("blogName") String blogName,
                @Query("api_key") String apiKey,
                @Query("limit") int count,
                @Query("before") long beforeTimestamp
        );
    }

    private void initRestApi(Context context) {
        if (_tumblrApi == null) {
            Retrofit.Builder retrofitBuilder = new Retrofit.Builder();

            // set json as in/out format
            retrofitBuilder.addConverterFactory(MoshiConverterFactory.create());

            // allow RxJava
            retrofitBuilder.addCallAdapterFactory(RxJavaCallAdapterFactory.create());

            _tumblrApi = retrofitBuilder
                    .baseUrl(context.getString(R.string.base_url))
                    .client(getOkHttpClient())
                    .build()
                    .create(TumblrApi.class);
        }
    }

    @NonNull
    private OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new ChunkedLogger());
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            clientBuilder.addInterceptor(httpLoggingInterceptor);
        }

        return clientBuilder.build();
    }

    private static class ChunkedLogger implements HttpLoggingInterceptor.Logger {

        private static final int LOG_CHUNK_SIZE = 4000;

        @Override
        public void log(@NonNull String message) {
            for (int i = 0, len = message.length(); i < len; i += LOG_CHUNK_SIZE) {
                int end = Math.min(len, i + LOG_CHUNK_SIZE);
                Log.d("Http", message.substring(i, end));
            }
        }
    }
}
