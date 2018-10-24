package nl.acidcats.tumblrlikes.core.constants

/**
 * Created by stephan on 17/05/2017.
 */
enum class FilterType(val isRandom: Boolean) {
    UNHIDDEN(true),
    FAVORITE(true),
    POPULAR(true),
    LATEST(false),
    LEAST_SEEN(false);

}//    MOST_TIME_PER_VIEW(R.string.filter_most_time_per_view, false)
