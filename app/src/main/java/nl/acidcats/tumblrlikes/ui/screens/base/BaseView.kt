package nl.acidcats.tumblrlikes.ui.screens.base

import android.content.Context

/**
 * Created on 18/10/2018.
 */
interface BaseView {
    fun sendBroadcast(action: String)

    fun clearArgument(key:String)

    fun showToast(message:String?)

    fun getContext():Context?
}
