package nl.acidcats.tumblrlikes.ui.screens.base

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import nl.acidcats.tumblrlikes.di.Injectable

/**
 * Created on 29/10/2018.
 */
abstract class BaseFragment : Fragment(), BaseView, Injectable {

    override fun sendBroadcast(action: String) {
        context?.let {
            LocalBroadcastManager.getInstance(it).sendBroadcast(Intent(action))
        }
    }

    override fun clearArgument(key: String) {
        arguments?.remove(key)
    }

    override fun showToast(message: String?) {
        if (context != null && message != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getContext(): Context? {
        return super.getContext()
    }
}