package nl.acidcats.tumblrlikes.core.repositories

/**
 * Created on 24/10/2018.
 */
interface AppDataRepository {
    fun getTumblrApiKey(): String

    fun setTumblrBlog(tumblrBlog: String)

    fun getTumblrBlog(): String?

    fun setPincodeHash(pinCode: String)

    fun getPincodeHash(): String?

    fun getLastLikeTime(): Long

    fun setLastLikeTime(time: Long)

    fun getAppStopTime(): Long

    fun setAppStopTime(appStopTime: Long)

    fun getLastCheckTime(): Long

    fun setLastCheckTime(time:Long)
}