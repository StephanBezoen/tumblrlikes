package nl.acidcats.tumblrlikes.data.repo.app.store;

import android.support.annotation.Nullable;

import com.pixplicity.easyprefs.library.Prefs;

import nl.acidcats.tumblrlikes.data.constants.PrefKeys;
import nl.acidcats.tumblrlikes.datalib.BuildConfig;

/**
 * Created by stephan on 29/04/2017.
 */

public class AppStoreImpl implements AppStore {
    private static final String TAG = AppStoreImpl.class.getSimpleName();

    private final boolean _debug = BuildConfig.DEBUG;

    @Override
    public void setTumblrApiKey(String tumblrApiKey) {
        Prefs.putString(PrefKeys.KEY_TUMBLR_API_KEY, tumblrApiKey);
    }

    @Override
    @Nullable
    public String getTumblrApiKey() {
        if (Prefs.contains(PrefKeys.KEY_TUMBLR_API_KEY)) {
            return Prefs.getString(PrefKeys.KEY_TUMBLR_API_KEY, null);
        } else {
            return BuildConfig.CONSUMER_KEY;
        }
    }

    @Override
    public void setTumblrBlog(String tumblrBlog) {
        Prefs.putString(PrefKeys.KEY_TUMBLR_BLOG, tumblrBlog);
    }

    @Override
    public String getTumblrBlog() {
        return Prefs.getString(PrefKeys.KEY_TUMBLR_BLOG, null);
    }

    @Override
    public void storePincodeHash(String pinCodeHash) {
        Prefs.putString(PrefKeys.KEY_PINCODE_HASH, pinCodeHash);
    }

    @Override
    public void clearPincodeHash() {
        Prefs.remove(PrefKeys.KEY_PINCODE_HASH);
    }

    @Override
    public String getPincodeHash() {
        return Prefs.getString(PrefKeys.KEY_PINCODE_HASH, null);
    }
}
