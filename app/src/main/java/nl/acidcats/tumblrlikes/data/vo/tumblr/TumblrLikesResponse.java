package nl.acidcats.tumblrlikes.data.vo.tumblr;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by stephan on 28/03/2017.
 */

@AutoValue
public abstract class TumblrLikesResponse {
    @SerializedName("liked_posts")
    public abstract List<TumblrLikeVO> likes();

    public static TypeAdapter<TumblrLikesResponse> typeAdapter(Gson gson) {
        return new AutoValue_TumblrLikesResponse.GsonTypeAdapter(gson);
    }
}
