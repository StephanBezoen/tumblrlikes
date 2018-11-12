package nl.acidcats.tumblrlikes.util.prefs

import android.content.Context

/**
 * Created on 25/10/2018.
 */
class PrefsHelper constructor(context: Context, name: String) {
    private val prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE)

    fun putString(key: String, value: String) = prefs.edit().putString(key, value).apply()

    fun getString(key: String, defValue: String? = null): String? = prefs.getString(key, defValue)

    fun putLong(key: String, value: Long) = prefs.edit().putLong(key, value).apply()

    fun getLong(key: String, defValue:Long = 0): Long = prefs.getLong(key, defValue)

    fun putBoolean(key:String, value:Boolean) = prefs.edit().putBoolean(key, value).apply()

    fun getBoolean(key:String, defValue:Boolean = false):Boolean = prefs.getBoolean(key, defValue)
}