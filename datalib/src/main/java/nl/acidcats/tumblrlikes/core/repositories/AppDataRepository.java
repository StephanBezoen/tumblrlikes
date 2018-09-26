package nl.acidcats.tumblrlikes.core.repositories;

import android.support.annotation.NonNull;

/**
 * Created by stephan on 29/04/2017.
 */

public interface AppDataRepository {
    boolean isSetupComplete();

    void setTumblrApiKey(String tumblrApiKey);

    String getTumblrApiKey();

    void setTumblrBlog(String tumblrBlog);

    String getTumblrBlog();

    void setPincode(String pinCode);

    void clearPincode();

    boolean hasPincode();

    boolean isPincodeCorrect(@NonNull String pinCode);

    void setCheckComplete();

    long getMostRecentCheckTime();

    boolean isTimeToCheck();

    void resetCheckTime();

    long getAppStopTime();

    void setAppStopTime(long appStopTime);
}
