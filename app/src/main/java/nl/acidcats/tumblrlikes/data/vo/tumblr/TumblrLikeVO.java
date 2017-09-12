package nl.acidcats.tumblrlikes.data.vo.tumblr;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by stephan on 28/03/2017.
 */

@AutoValue
public abstract class TumblrLikeVO {
    public abstract String type();

    public abstract long id();

    @SerializedName("liked_timestamp")
    public abstract long timestamp();

    @Nullable
    public abstract List<TumblrPhotoPostVO> photos();

    @SerializedName("post_url")
    public abstract String postUrl();

    public boolean isPhoto() {
        return type().equals(TumblrPostType.PHOTO);
    }


    public static TypeAdapter<TumblrLikeVO> typeAdapter(Gson gson) {
        return new AutoValue_TumblrLikeVO.GsonTypeAdapter(gson);
    }

    /**
     * Created by stephan on 11/04/2017.
     */

    private static class TumblrPostType {
        static final String PHOTO = "photo";
        static final String VIDEO = "video";

    }
}
