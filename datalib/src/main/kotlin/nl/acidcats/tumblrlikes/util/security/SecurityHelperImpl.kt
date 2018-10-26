package nl.acidcats.tumblrlikes.util.security

import java.security.MessageDigest

/**
 * Created on 26/10/2018.
 */
class SecurityHelperImpl: SecurityHelper {
    private val digest: MessageDigest? = MessageDigest.getInstance("MD5")

    override fun getHash(text: String): String {
        if (digest == null) return text;

        digest.update(text.toByteArray())

        val messageDigest = digest.digest()
        val hexString = StringBuilder()
        for (byte:Byte in messageDigest) {
            var h = (0xFF and byte.toInt()).toString(16)
            while (h.length < 2) h = "0$h"
            hexString.append(h)
        }

        return hexString.toString()
    }
}