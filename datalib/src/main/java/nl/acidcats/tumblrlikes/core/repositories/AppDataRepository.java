package nl.acidcats.tumblrlikes.core.repositories;

import android.support.annotation.Nullable;

/**
 * Created by stephan on 29/04/2017.
 */

public interface AppDataRepository {
    String getTumblrApiKey();

    void setTumblrBlog(String tumblrBlog);

    String getTumblrBlog();

    void setPincodeHash(String pinCode);

    @Nullable
    String getPincodeHash();

    long getLatestCheckTimestamp();

    void setLatestCheckTimestamp(long time);

    long getAppStopTime();

    void setAppStopTime(long appStopTime);
}
