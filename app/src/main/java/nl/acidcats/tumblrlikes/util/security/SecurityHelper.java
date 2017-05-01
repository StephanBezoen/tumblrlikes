package nl.acidcats.tumblrlikes.util.security;

import android.support.annotation.NonNull;

/**
 * Created by stephan on 13/04/2017.
 */

public interface SecurityHelper {
    @NonNull
    String getHash (@NonNull String text);
}
