package nl.acidcats.tumblrlikes.core.constants;

import android.support.annotation.StringRes;

import nl.acidcats.tumblrlikes.datalib.R;

/**
 * Created by stephan on 17/05/2017.
 */
public enum FilterType {
    UNHIDDEN(R.string.filter_all, true),
    FAVORITE(R.string.filter_favorite, true),
    POPULAR(R.string.filter_popular, true),
    LATEST(R.string.filter_latest, false),
    LEAST_SEEN(R.string.filter_least_seen, false),
//    MOST_TIME_PER_VIEW(R.string.filter_most_time_per_view, false)
    ;

    private final int _resId;
    private final boolean _isRandom;

    FilterType(@StringRes int resId, boolean isRandom) {
        _resId = resId;
        _isRandom = isRandom;
    }

    @StringRes
    public int getResId() {
        return _resId;
    }

    public boolean isRandom() {
        return _isRandom;
    }

    public boolean isLinear() {
        return !_isRandom;
    }
}
