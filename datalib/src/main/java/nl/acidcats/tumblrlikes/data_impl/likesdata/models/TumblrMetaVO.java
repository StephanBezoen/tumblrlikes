package nl.acidcats.tumblrlikes.data_impl.likesdata.models;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

/**
 * Created by stephan on 28/03/2017.
 */

@AutoValue
public abstract class TumblrMetaVO {
    public abstract int status();

    @Json(name = "msg")
    public abstract String message();

    public static JsonAdapter<TumblrMetaVO> jsonAdapter(Moshi moshi) {
        return new AutoValue_TumblrMetaVO.MoshiJsonAdapter(moshi);
    }
}
