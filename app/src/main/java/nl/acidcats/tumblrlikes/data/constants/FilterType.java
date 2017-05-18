package nl.acidcats.tumblrlikes.data.constants;

import android.support.annotation.StringRes;

import nl.acidcats.tumblrlikes.R;

/**
 * Created by stephan on 17/05/2017.
 */
public enum FilterType {
    UNHIDDEN(R.string.filter_all),
    FAVORITE(R.string.filter_favorite),
    POPULAR(R.string.filter_popular);

    private final int _resId;

    FilterType(@StringRes int resId) {
        _resId = resId;
    }

    @StringRes
    public int getResId() {
        return _resId;
    }
}
