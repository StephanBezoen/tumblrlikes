package nl.acidcats.tumblrlikes.util

import android.os.Build

/**
 * Created on 20/11/2018.
 */
class DeviceUtil {
    companion object {
        val isEmulator: Boolean
            get() {
/*
            Timber.d { "fingerprint: ${Build.FINGERPRINT}" }
            Timber.d { "model: ${Build.MODEL}" }
            Timber.d { "manufacturer: ${Build.MANUFACTURER}" }
            Timber.d { "brand: ${Build.BRAND}" }
            Timber.d { "hardware: ${Build.HARDWARE}" }
            Timber.d { "board: ${Build.BOARD}" }
            Timber.d { "brand: ${Build.BRAND}" }
            Timber.d { "device: ${Build.DEVICE}" }
            Timber.d { "product: ${Build.PRODUCT}" }
            Timber.d { "tags: ${Build.TAGS}" }
            Timber.d { "type: ${Build.TYPE}" }
            Timber.d { "radio version: ${Build.getRadioVersion()}" }
*/

                return Build.DEVICE.contains("generic")
            }

    }
}