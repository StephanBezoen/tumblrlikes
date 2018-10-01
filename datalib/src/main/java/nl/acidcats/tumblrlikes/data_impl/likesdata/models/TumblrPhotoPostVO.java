package nl.acidcats.tumblrlikes.data_impl.likesdata.models;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.List;

/**
 * Created by stephan on 11/04/2017.
 */
@AutoValue
public abstract class TumblrPhotoPostVO {
    @Json(name = "original_size")
    public abstract TumblrPhotoVO originalPhoto();

    @Json(name = "alt_sizes")
    public abstract List<TumblrPhotoVO> altPhotos();

    public static JsonAdapter<TumblrPhotoPostVO> jsonAdapter(Moshi moshi) {
        return new AutoValue_TumblrPhotoPostVO.MoshiJsonAdapter(moshi);
    }
}
