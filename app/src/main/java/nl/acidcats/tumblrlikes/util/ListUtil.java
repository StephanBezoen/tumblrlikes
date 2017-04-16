package nl.acidcats.tumblrlikes.util;

import android.support.annotation.Nullable;

import java.util.List;

/**
 * Created by stephan on 16/04/2017.
 */

public class ListUtil {
    @Nullable
    public static <T> T getFirstFromList(List<T> items) {
        return (items != null && items.size() > 0) ? items.get(0) : null;
    }
}
