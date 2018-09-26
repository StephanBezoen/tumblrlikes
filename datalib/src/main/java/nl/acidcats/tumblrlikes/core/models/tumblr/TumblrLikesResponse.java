package nl.acidcats.tumblrlikes.core.models.tumblr;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.List;

/**
 * Created by stephan on 28/03/2017.
 */

@AutoValue
public abstract class TumblrLikesResponse {
    @Json(name = "liked_posts")
    public abstract List<TumblrLikeVO> likes();

    public static JsonAdapter<TumblrLikesResponse> jsonAdapter(Moshi moshi) {
        return new AutoValue_TumblrLikesResponse.MoshiJsonAdapter(moshi);
    }
}
