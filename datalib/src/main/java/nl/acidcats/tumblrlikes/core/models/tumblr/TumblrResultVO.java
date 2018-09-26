package nl.acidcats.tumblrlikes.core.models.tumblr;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.lang.reflect.Type;

/**
 * Created by stephan on 28/03/2017.
 */

@AutoValue
public abstract class TumblrResultVO<T> {
    @Json(name = "meta")
    public abstract TumblrMetaVO metaData();

    @Json(name = "response")
    public abstract T response();

    public static <T> JsonAdapter<TumblrResultVO<T>> jsonAdapter(Moshi moshi, Type[] types) {
        return new AutoValue_TumblrResultVO.MoshiJsonAdapter(moshi, types);
    }
}
