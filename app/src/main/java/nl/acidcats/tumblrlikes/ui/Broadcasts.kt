package nl.acidcats.tumblrlikes.ui

/**
 * Created by stephan on 13/04/2017.
 */

object Broadcasts {
    private val PREFIX = Broadcasts::class.java.canonicalName

    val PINCODE_OK = "$PREFIX.pincodeOk"
    val PINCODE_SETUP_OK = "$PREFIX.pincodeSetupOk"
    val ALL_LIKES_LOADED = "$PREFIX.allLikesLoaded"
    val DATABASE_RESET = "$PREFIX.databaseReset"
    val SETUP_COMPLETE = "$PREFIX.setupComplete"
    val REFRESH_ALL_REQUEST = "$PREFIX.refreshAllRequest"
    val SETTINGS_REQUEST = "$PREFIX.settingsRequest"
    val CACHE_SERVICE_REQUEST = "$PREFIX.cacheServiceRequest"
}
