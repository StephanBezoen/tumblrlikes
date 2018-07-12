package nl.acidcats.tumblrlikes.data.repo.app;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import javax.inject.Inject;

import nl.acidcats.tumblrlikes.data.repo.app.store.AppStore;
import nl.acidcats.tumblrlikes.datalib.BuildConfig;
import nl.acidcats.tumblrlikes.util.security.SecurityHelper;

/**
 * Created by stephan on 29/04/2017.
 */

public class AppRepoImpl implements AppRepo {
    private static final String TAG = AppRepoImpl.class.getSimpleName();

    @Inject
    AppStore _appStore;
    @Inject
    SecurityHelper _securityHelper;

    private final boolean _debug = BuildConfig.DEBUG;

    @Inject
    public AppRepoImpl() {
    }

    @Override
    public boolean isSetupComplete() {
        String apiKey = getTumblrApiKey();
        String blog = getTumblrBlog();

        return !TextUtils.isEmpty(apiKey) && !TextUtils.isEmpty(blog);
    }

    @Override
    public void setTumblrApiKey(String tumblrApiKey) {
        _appStore.setTumblrApiKey(tumblrApiKey);
    }

    @Override
    public String getTumblrApiKey() {
        return _appStore.getTumblrApiKey();
    }

    @Override
    public void setTumblrBlog(String tumblrBlog) {
        _appStore.setTumblrBlog(tumblrBlog);
    }

    @Override
    public String getTumblrBlog() {
        return _appStore.getTumblrBlog();
    }

    @Override
    public void setPincode(String pinCode) {
        _appStore.storePincodeHash(_securityHelper.getHash(pinCode));
    }

    @Override
    public void clearPincode() {
        _appStore.clearPincodeHash();
    }

    @Override
    public boolean hasPincode() {
        return (_appStore.getPincodeHash() != null);
    }

    @Override
    public boolean isPincodeCorrect(@NonNull String pinCode) {
        if (!hasPincode()) return true;

        String pinCodeHash = _securityHelper.getHash(pinCode);
        String storedPinCodeHash = _appStore.getPincodeHash();
        return pinCodeHash.equals(storedPinCodeHash);
    }
}
