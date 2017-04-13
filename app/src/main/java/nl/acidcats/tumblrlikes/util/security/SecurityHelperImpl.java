package nl.acidcats.tumblrlikes.util.security;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import nl.acidcats.tumblrlikes.BuildConfig;

/**
 * Created by stephan on 13/04/2017.
 */

public class SecurityHelperImpl implements SecurityHelper {
    private static final String TAG = SecurityHelperImpl.class.getSimpleName();
    private MessageDigest _digest;

    public SecurityHelperImpl() {
        try {
            _digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Exception while getting digest", e);
        }
    }

    @Override
    public boolean checkPassword(String password) {
        _digest.update(password.getBytes());
        byte messageDigest[] = _digest.digest();

        StringBuilder hexString = new StringBuilder();
        for (byte aMessageDigest : messageDigest) {
            String h = Integer.toHexString(0xFF & aMessageDigest);
            while (h.length() < 2)
                h = "0" + h;
            hexString.append(h);
        }

        return hexString.toString().equals(BuildConfig.PINHASH);
    }
}
