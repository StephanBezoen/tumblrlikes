package nl.acidcats.tumblrlikes.data_impl.appdata;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.util.Date;

import javax.inject.Inject;

import nl.acidcats.tumblrlikes.core.repositories.AppDataRepository;
import nl.acidcats.tumblrlikes.core.repositories.gateways.AppDataGateway;
import nl.acidcats.tumblrlikes.datalib.BuildConfig;
import nl.acidcats.tumblrlikes.util.security.SecurityHelper;

/**
 * Created by stephan on 29/04/2017.
 */

public class AppDataRepositoryImpl implements AppDataRepository {
    private static final String TAG = AppDataRepositoryImpl.class.getSimpleName();

    private static final long TIME_BETWEEN_CHECKS_MS = 24L * 60L * 60L * 1000L; // 24 hours

    private AppDataGateway _appDataGateway;
    private SecurityHelper _securityHelper;

    private final boolean _debug = BuildConfig.DEBUG;

    @Inject
    public AppDataRepositoryImpl(AppDataGateway appDataGateway, SecurityHelper securityHelper) {
        _appDataGateway = appDataGateway;
        _securityHelper = securityHelper;
    }

    @Override
    public boolean isSetupComplete() {
        String apiKey = getTumblrApiKey();
        String blog = getTumblrBlog();

        return !TextUtils.isEmpty(apiKey) && !TextUtils.isEmpty(blog);
    }

    @Override
    public void setTumblrApiKey(String tumblrApiKey) {
        _appDataGateway.setTumblrApiKey(tumblrApiKey);
    }

    @Override
    public String getTumblrApiKey() {
        return _appDataGateway.getTumblrApiKey();
    }

    @Override
    public void setTumblrBlog(String tumblrBlog) {
        _appDataGateway.setTumblrBlog(tumblrBlog);
    }

    @Override
    public String getTumblrBlog() {
        return _appDataGateway.getTumblrBlog();
    }

    @Override
    public void setPincode(String pinCode) {
        _appDataGateway.storePincodeHash(_securityHelper.getHash(pinCode));
    }

    @Override
    public void clearPincode() {
        _appDataGateway.clearPincodeHash();
    }

    @Override
    public boolean hasPincode() {
        return (_appDataGateway.getPincodeHash() != null);
    }

    @Override
    public boolean isPincodeCorrect(@NonNull String pinCode) {
        if (!hasPincode()) return true;

        String pinCodeHash = _securityHelper.getHash(pinCode);
        String storedPinCodeHash = _appDataGateway.getPincodeHash();
        return pinCodeHash.equals(storedPinCodeHash);
    }

    @Override
    public void setCheckComplete() {
        _appDataGateway.setLatestCheckTimestamp(new Date().getTime());
    }

    @Override
    public long getMostRecentCheckTime() {
        return _appDataGateway.getLatestCheckTimestamp();
    }

    @Override
    public boolean isTimeToCheck() {
        long timeSinceLastCheck = new Date().getTime() - getMostRecentCheckTime();
        return timeSinceLastCheck > TIME_BETWEEN_CHECKS_MS;
    }

    @Override
    public void resetCheckTime() {
        if (_debug) Log.d(TAG, "reset: ");

        _appDataGateway.setLatestCheckTimestamp(0L);
    }

    @Override
    public long getAppStopTime() {
        return _appDataGateway.getAppStopTime();
    }

    @Override
    public void setAppStopTime(long appStopTime) {
        _appDataGateway.setAppStopTime(appStopTime);
    }
}
