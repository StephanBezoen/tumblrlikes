package nl.acidcats.tumblrlikes.data_impl.likesdata.models;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.List;

/**
 * Created by stephan on 28/03/2017.
 */

@AutoValue
public abstract class TumblrLikeVO {
    public abstract String type();

    public abstract long id();

    @Json(name = "liked_timestamp")
    public abstract long timestamp();

    @Nullable
    public abstract List<TumblrPhotoPostVO> photos();

    @Json(name = "post_url")
    public abstract String postUrl();

    public boolean isPhoto() {
        return type().equals(TumblrPostType.PHOTO);
    }

    public static JsonAdapter<TumblrLikeVO> jsonAdapter(Moshi moshi) {
        return new AutoValue_TumblrLikeVO.MoshiJsonAdapter(moshi);
    }

    /**
     * Created by stephan on 11/04/2017.
     */

    private static class TumblrPostType {
        static final String PHOTO = "photo";
        static final String VIDEO = "video";

    }
}
