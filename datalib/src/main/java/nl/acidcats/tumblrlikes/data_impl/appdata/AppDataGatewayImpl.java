package nl.acidcats.tumblrlikes.data_impl.appdata;

import android.support.annotation.Nullable;

import com.pixplicity.easyprefs.library.Prefs;

import nl.acidcats.tumblrlikes.core.repositories.gateways.AppDataGateway;
import nl.acidcats.tumblrlikes.datalib.BuildConfig;

/**
 * Created by stephan on 29/04/2017.
 */

public class AppDataGatewayImpl implements AppDataGateway {
    private static final String TAG = AppDataGatewayImpl.class.getSimpleName();

    private static final String KEY_APP_STOP_TIME = "key_appStopTime";
    private static final String KEY_TUMBLR_API_KEY = "key_tumblrApiKey";
    private static final String KEY_TUMBLR_BLOG = "key_tumblrBlog";
    private static final String KEY_PINCODE_HASH = "key_pincodeHash";
    private static final String KEY_LATEST_CHECK_TIMESTAMP = "key_latestCheckTimestamp";


    @Override
    public void setTumblrApiKey(String tumblrApiKey) {
        Prefs.putString(KEY_TUMBLR_API_KEY, tumblrApiKey);
    }

    @Override
    @Nullable
    public String getTumblrApiKey() {
        if (Prefs.contains(KEY_TUMBLR_API_KEY)) {
            return Prefs.getString(KEY_TUMBLR_API_KEY, null);
        } else {
            return BuildConfig.CONSUMER_KEY;
        }
    }

    @Override
    public void setTumblrBlog(String tumblrBlog) {
        Prefs.putString(KEY_TUMBLR_BLOG, tumblrBlog);
    }

    @Override
    public String getTumblrBlog() {
        return Prefs.getString(KEY_TUMBLR_BLOG, null);
    }

    @Override
    public void storePincodeHash(String pinCodeHash) {
        Prefs.putString(KEY_PINCODE_HASH, pinCodeHash);
    }

    @Override
    public void clearPincodeHash() {
        Prefs.remove(KEY_PINCODE_HASH);
    }

    @Override
    public String getPincodeHash() {
        return Prefs.getString(KEY_PINCODE_HASH, null);
    }

    @Override
    public long getAppStopTime() {
        return Prefs.getLong(KEY_APP_STOP_TIME, 0L);
    }

    @Override
    public void setAppStopTime(long appStopTime) {
        Prefs.putLong(KEY_APP_STOP_TIME, appStopTime);
    }

    @Override
    public void setLatestCheckTimestamp(long latestCheckTimestamp) {
        Prefs.putLong(KEY_LATEST_CHECK_TIMESTAMP, latestCheckTimestamp);
    }

    @Override
    public long getLatestCheckTimestamp() {
        return Prefs.getLong(KEY_LATEST_CHECK_TIMESTAMP, 0L);
    }
}
