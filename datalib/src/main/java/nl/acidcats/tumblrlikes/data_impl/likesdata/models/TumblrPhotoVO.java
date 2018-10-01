package nl.acidcats.tumblrlikes.data_impl.likesdata.models;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

/**
 * Created by stephan on 11/04/2017.
 */
@AutoValue
public abstract class TumblrPhotoVO {
    public abstract String url();

    public abstract int width();

    public abstract int height();

    public long getSize() {
        return (long) width() * (long) height();
    }

    public static JsonAdapter<TumblrPhotoVO> jsonAdapter(Moshi moshi) {
        return new AutoValue_TumblrPhotoVO.MoshiJsonAdapter(moshi);
    }
}
