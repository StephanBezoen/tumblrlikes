package nl.acidcats.tumblrlikes.data.vo.tumblr;

import com.ryanharter.auto.value.moshi.MoshiAdapterFactory;
import com.squareup.moshi.JsonAdapter;

/**
 * Created on 11/07/2018.
 */
@MoshiAdapterFactory
public abstract class TumblrMoshiAdapterFactory implements JsonAdapter.Factory {
    public static JsonAdapter.Factory create() {
        return new AutoValueMoshi_TumblrMoshiAdapterFactory();
    }
}
