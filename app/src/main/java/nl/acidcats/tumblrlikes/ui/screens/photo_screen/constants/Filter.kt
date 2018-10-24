package nl.acidcats.tumblrlikes.ui.screens.photo_screen.constants

import android.support.annotation.StringRes
import nl.acidcats.tumblrlikes.core.constants.FilterType
import nl.acidcats.tumblrlikes.datalib.R

/**
 * Created on 24/10/2018.
 */
enum class Filter(@param:StringRes @get:StringRes val resId: Int, val filterType: FilterType) {
    UNHIDDEN(R.string.filter_all, FilterType.UNHIDDEN),
    FAVORITE(R.string.filter_favorite, FilterType.FAVORITE),
    POPULAR(R.string.filter_popular, FilterType.POPULAR),
    LATEST(R.string.filter_latest, FilterType.LATEST),
    LEAST_SEEN(R.string.filter_least_seen, FilterType.LEAST_SEEN);

    companion object {
        @JvmStatic
        fun getFilterByType(filterType: FilterType): Filter {
            for (filter in Filter.values()) {
                if (filter.filterType == filterType) {
                    return filter;
                }
            }

            return UNHIDDEN;
        }
    }

}