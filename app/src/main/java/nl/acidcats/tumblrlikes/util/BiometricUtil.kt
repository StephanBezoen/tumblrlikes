package nl.acidcats.tumblrlikes.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.core.hardware.fingerprint.FingerprintManagerCompat

/**
 * Created on 20/11/2018.
 */
@SuppressLint("MissingPermission")
class BiometricUtil {
    companion object {
        fun isPromptEnabled(): Boolean =
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

        fun isSdkSupported(): Boolean =
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

        fun isHardwareSupported(context: Context): Boolean =
                FingerprintManagerCompat.from(context).isHardwareDetected

        fun isFingerprintAvailable(context: Context): Boolean =
                FingerprintManagerCompat.from(context).hasEnrolledFingerprints()
    }
}
