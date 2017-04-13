package nl.acidcats.tumblrlikes.data.repo.like.store;

import android.content.Context;
import android.util.Log;

import com.google.gson.GsonBuilder;

import java.util.List;

import nl.acidcats.tumblrlikes.BuildConfig;
import nl.acidcats.tumblrlikes.R;
import nl.acidcats.tumblrlikes.data.vo.tumblr.TumblrAdapterFactory;
import nl.acidcats.tumblrlikes.data.vo.tumblr.TumblrLikeVO;
import nl.acidcats.tumblrlikes.data.vo.tumblr.TumblrLikesResponse;
import nl.acidcats.tumblrlikes.data.vo.tumblr.TumblrResultVO;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

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

    private TumblrApi initRestApi(Context context) {
        if (_tumblrApi == null) {
            Retrofit.Builder builder = new Retrofit.Builder();

            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(message -> Log.d("OkHttp", message));
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            clientBuilder.addInterceptor(httpLoggingInterceptor);

            // set json as in/out format
            builder.addConverterFactory(GsonConverterFactory.create(
                    new GsonBuilder().registerTypeAdapterFactory(TumblrAdapterFactory.create()).create()));

            // allow RxJava
            builder.addCallAdapterFactory(RxJavaCallAdapterFactory.create());

            _tumblrApi = builder
                    .baseUrl(context.getString(R.string.base_url))
                    .client(clientBuilder.build())
                    .build()
                    .create(TumblrApi.class);
        }

        return _tumblrApi;
    }
}
