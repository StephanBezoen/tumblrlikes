package nl.acidcats.tumblrlikes.util.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created on 04/10/2018.
 */
public class PrefsHelper {

    private SharedPreferences _prefs;

    public PrefsHelper(Context context, @NonNull String name) {
        _prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public void putString(String key, String value) {
        _prefs.edit().putString(key, value).apply();
    }

    public void putLong(String key, long value) {
        _prefs.edit().putLong(key, value).apply();
    }

    public String getString(String key) {
        return getString(key, null);
    }

    public String getString(String key, @Nullable String defValue) {
        return _prefs.getString(key, defValue);
    }

    public long getLong(String key) {
        return _prefs.getLong(key, 0L);
    }
}
