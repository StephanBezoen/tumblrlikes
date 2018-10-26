package nl.acidcats.tumblrlikes.data_impl.likesdata.gateway

import android.content.Context
import android.util.Log
import nl.acidcats.tumblrlikes.data_impl.likesdata.models.TumblrLikeVO
import nl.acidcats.tumblrlikes.data_impl.likesdata.models.TumblrLikesResponse
import nl.acidcats.tumblrlikes.data_impl.likesdata.models.TumblrResultVO
import nl.acidcats.tumblrlikes.datalib.BuildConfig
import nl.acidcats.tumblrlikes.datalib.R
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import rx.Observable
import rx.schedulers.Schedulers

/**
 * Created on 25/10/2018.
 */
class NetLikesDataGateway(context: Context) : LikesDataGateway {

    private val tumblrApi: TumblrApi
    private val apiKey = BuildConfig.CONSUMER_KEY

    init {
        val builder = Retrofit.Builder()
        builder.addConverterFactory(MoshiConverterFactory.create())
        builder.addCallAdapterFactory(RxJavaCallAdapterFactory.create())

        tumblrApi = builder
                .baseUrl(context.getString(R.string.base_url))
                .client(getOkHttpClient())
                .build()
                .create(TumblrApi::class.java)
    }

    fun getOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor(ChunkedLogger())
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(loggingInterceptor)
        }

        return builder.build()
    }

    override fun getLikes(blogName: String, count: Int, beforeTime: Long): Observable<List<TumblrLikeVO>> {
        return tumblrApi
                .getLikes(blogName, apiKey, count, beforeTime)
                .subscribeOn(Schedulers.io())
                .map {
                    it.response.likes
                }
    }

    interface TumblrApi {
        @GET("{blogName}/likes")
        fun getLikes(@Path("blogName") blogName: String,
                     @Query("api_key") apiKey: String,
                     @Query("limit") count: Int,
                     @Query("before") beforeTimestamp: Long)
                : Observable<TumblrResultVO<TumblrLikesResponse>>
    }

    private class ChunkedLogger : HttpLoggingInterceptor.Logger {

        val LOG_CHUNK_SIZE = 4000

        override fun log(message: String) {
            val len = message.length
            for (i in 0..len step LOG_CHUNK_SIZE) {
                val end = Math.min(len, i + LOG_CHUNK_SIZE)
                Log.d("Http", message.substring(i, end))
            }
        }
    }
}