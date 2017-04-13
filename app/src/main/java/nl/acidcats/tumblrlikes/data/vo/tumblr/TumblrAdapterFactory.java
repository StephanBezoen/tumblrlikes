package nl.acidcats.tumblrlikes.data.vo.tumblr;

import com.google.gson.TypeAdapterFactory;
import com.ryanharter.auto.value.gson.GsonTypeAdapterFactory;

/**
 * Created by stephan on 28/03/2017.
 */

@GsonTypeAdapterFactory
public abstract class TumblrAdapterFactory implements TypeAdapterFactory {
    public static TypeAdapterFactory create() {
        return new AutoValueGson_TumblrAdapterFactory();
    }
}
