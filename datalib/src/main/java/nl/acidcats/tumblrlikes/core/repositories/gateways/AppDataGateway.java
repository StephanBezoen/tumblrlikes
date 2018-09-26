package nl.acidcats.tumblrlikes.core.repositories.gateways;

import android.support.annotation.Nullable;

/**
 * Created by stephan on 29/04/2017.
 */

public interface AppDataGateway {
    void setTumblrApiKey(String tumblrApiKey);

    @Nullable
    String getTumblrApiKey();

    void setTumblrBlog(String tumblrBlog);

    @Nullable
    String getTumblrBlog();

    void storePincodeHash(String pinCodeHash);

    void clearPincodeHash();

    @Nullable
    String getPincodeHash();

    long getAppStopTime();

    void setAppStopTime(long appStopTime);

    void setLatestCheckTimestamp(long latestCheckTimestamp);

    long getLatestCheckTimestamp();
}
