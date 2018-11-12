package nl.acidcats.tumblrlikes.data_impl.appdata

import android.content.Context
import nl.acidcats.tumblrlikes.core.repositories.AppDataRepository
import nl.acidcats.tumblrlikes.datalib.BuildConfig
import nl.acidcats.tumblrlikes.util.prefs.PrefsHelper
import javax.inject.Inject

/**
 * Created on 25/10/2018.
 */
class AppDataRepositoryImpl @Inject constructor(context: Context) : AppDataRepository {

    private val prefsHelper = PrefsHelper(context, context.packageName)

    private enum class Keys(val value: String) {
        KEY_APP_STOP_TIME("key_appStopTime"),
        KEY_TUMBLR_API_KEY("key_tumblrApiKey"),
        KEY_TUMBLR_BLOG("key_tumblrBlog"),
        KEY_PINCODE_HASH("key_pincodeHash"),
        KEY_LATEST_CHECK_TIMESTAMP("key_latestCheckTimestamp"),
        KEY_LAST_LIKE_TIME("key_lastLikeTime")
    }

    override fun getTumblrApiKey(): String = prefsHelper.getString(Keys.KEY_TUMBLR_API_KEY.value, BuildConfig.CONSUMER_KEY)!!

    override fun setTumblrBlog(tumblrBlog: String) = prefsHelper.putString(Keys.KEY_TUMBLR_BLOG.value, tumblrBlog)

    override fun getTumblrBlog(): String? = prefsHelper.getString(Keys.KEY_TUMBLR_BLOG.value)

    override fun setPincodeHash(pinCode: String) = prefsHelper.putString(Keys.KEY_PINCODE_HASH.value, pinCode)

    override fun getPincodeHash(): String? = prefsHelper.getString(Keys.KEY_PINCODE_HASH.value)

    override fun getLastLikeTime(): Long = prefsHelper.getLong(Keys.KEY_LAST_LIKE_TIME.value, 1L)

    override fun setLastLikeTime(time: Long) = prefsHelper.putLong(Keys.KEY_LAST_LIKE_TIME.value, time)

    override fun getAppStopTime(): Long = prefsHelper.getLong(Keys.KEY_APP_STOP_TIME.value)

    override fun setAppStopTime(appStopTime: Long) = prefsHelper.putLong(Keys.KEY_APP_STOP_TIME.value, appStopTime)

    override fun getLastCheckTime(): Long = prefsHelper.getLong(Keys.KEY_LATEST_CHECK_TIMESTAMP.value)

    override fun setLastCheckTime(time: Long) = prefsHelper.putLong(Keys.KEY_LATEST_CHECK_TIMESTAMP.value, time)
}