package nl.acidcats.tumblrlikes.data.repo.like.store;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.squareup.moshi.Moshi;

import java.util.List;

import nl.acidcats.tumblrlikes.data.vo.tumblr.TumblrLikeVO;
import nl.acidcats.tumblrlikes.data.vo.tumblr.TumblrLikesResponse;
import nl.acidcats.tumblrlikes.data.vo.tumblr.TumblrMoshiAdapterFactory;
import nl.acidcats.tumblrlikes.data.vo.tumblr.TumblrResultVO;
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

public class NetLikesStore implements LikesStore {
    private static final String TAG = NetLikesStore.class.getSimpleName();

    private TumblrApi _tumblrApi;
    private String _apiKey;

    public NetLikesStore(Context context) {
        initRestApi(context);

        _apiKey = BuildConfig.CONSUMER_KEY;
    }

    @Override
    public Observable<List<TumblrLikeVO>> getLikes(String blogName, int count, long beforeTime) {
        return _tumblrApi
                .getLikes(blogName, _apiKey, count, beforeTime)
                .subscribeOn(Schedulers.io())
                .map(result -> result.response().likes());
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
            retrofitBuilder.addConverterFactory(MoshiConverterFactory.create(new Moshi.Builder().add(TumblrMoshiAdapterFactory.create()).build()));

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
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(message -> Log.d("OkHttp", message));
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            clientBuilder.addInterceptor(httpLoggingInterceptor);
        }

        return clientBuilder.build();
    }
}
