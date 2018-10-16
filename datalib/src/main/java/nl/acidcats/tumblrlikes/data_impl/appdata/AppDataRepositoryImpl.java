package nl.acidcats.tumblrlikes.data_impl.appdata;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.Date;

import javax.inject.Inject;

import nl.acidcats.tumblrlikes.core.repositories.AppDataRepository;
import nl.acidcats.tumblrlikes.datalib.BuildConfig;
import nl.acidcats.tumblrlikes.util.prefs.PrefsHelper;

/**
 * Created by stephan on 29/04/2017.
 */

public class AppDataRepositoryImpl implements AppDataRepository {
    private static final String TAG = AppDataRepositoryImpl.class.getSimpleName();

    private static final long TIME_BETWEEN_CHECKS_MS = 24L * 60L * 60L * 1000L; // 24 hours

    private static final String KEY_APP_STOP_TIME = "key_appStopTime";
    private static final String KEY_TUMBLR_API_KEY = "key_tumblrApiKey";
    private static final String KEY_TUMBLR_BLOG = "key_tumblrBlog";
    private static final String KEY_PINCODE_HASH = "key_pincodeHash";
    private static final String KEY_LATEST_CHECK_TIMESTAMP = "key_latestCheckTimestamp";

    private final PrefsHelper _prefsHelper;
    private final boolean _debug = BuildConfig.DEBUG;

    @Inject
    public AppDataRepositoryImpl(Context context) {
        _prefsHelper = new PrefsHelper(context, context.getPackageName());
    }

    @Override
    public boolean isSetupComplete() {
        String apiKey = getTumblrApiKey();
        String blog = getTumblrBlog();

        return !TextUtils.isEmpty(apiKey) && !TextUtils.isEmpty(blog);
    }

    @Override
    public String getTumblrApiKey() {
        return _prefsHelper.getString(KEY_TUMBLR_API_KEY, BuildConfig.CONSUMER_KEY);
    }

    @Override
    public void setTumblrBlog(String tumblrBlog) {
        _prefsHelper.putString(KEY_TUMBLR_BLOG, tumblrBlog);
    }

    @Override
    public String getTumblrBlog() {
        return _prefsHelper.getString(KEY_TUMBLR_BLOG);
    }

    @Override
    public void setPincodeHash(String pincodeHash) {
        _prefsHelper.putString(KEY_PINCODE_HASH, pincodeHash);
    }

    @Override
    @Nullable
    public String getPincodeHash() {
        return _prefsHelper.getString(KEY_PINCODE_HASH);
    }

    @Override
    public long getLatestCheckTimestamp() {
        return _prefsHelper.getLong(KEY_LATEST_CHECK_TIMESTAMP);
    }

    @Override
    public void setLatestCheckTimestamp(long time) {
        _prefsHelper.putLong(KEY_LATEST_CHECK_TIMESTAMP, time);
    }

    @Override
    public long getAppStopTime() {
        return _prefsHelper.getLong(KEY_APP_STOP_TIME);
    }

    @Override
    public void setAppStopTime(long appStopTime) {
        _prefsHelper.putLong(KEY_APP_STOP_TIME, appStopTime);
    }
}
