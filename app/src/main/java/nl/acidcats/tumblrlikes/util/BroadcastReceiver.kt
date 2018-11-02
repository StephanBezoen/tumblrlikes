package nl.acidcats.tumblrlikes.util

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager

/**
 * Created on 02/11/2018.
 */

typealias ActionHandler = (Intent) -> Unit

class BroadcastReceiver constructor(context: Context) : android.content.BroadcastReceiver() {

    private val intentFilter: IntentFilter = IntentFilter()
    private val actionHandlerMap = HashMap<String, ActionHandler>()
    private var isRegistered = false
    private var isDestroyed = false
    private val localBroadcastManager = LocalBroadcastManager.getInstance(context)

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action

        for ((actionKey, actionHandler) in actionHandlerMap) {
            if (actionKey == action) {
                actionHandler.invoke(intent)
                break
            }
        }
    }

    fun onResume() {
        if (isDestroyed || isRegistered) return

        localBroadcastManager.registerReceiver(this, intentFilter)

        isRegistered = true
    }

    fun onPause() {
        unregisterReceiver()
    }

    private fun unregisterReceiver() {
        if (isDestroyed || !isRegistered) return

        localBroadcastManager.unregisterReceiver(this)

        isRegistered = false
    }

    fun addActionHandler(action: String, handler: ActionHandler) {
        if (actionHandlerMap.containsKey(action)) {
            throw Error("Action $action already added to map")
        }

        actionHandlerMap.put(action, handler)

        intentFilter.addAction(action)
    }

    fun onDestroy() {
        unregisterReceiver()

        actionHandlerMap.clear()

        isDestroyed = true
    }
}
