package nl.acidcats.tumblrlikes.data.repo.app.store;

import android.support.annotation.Nullable;

/**
 * Created by stephan on 29/04/2017.
 */

public interface AppStore {
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
}
