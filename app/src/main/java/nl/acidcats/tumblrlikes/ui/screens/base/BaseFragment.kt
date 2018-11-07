package nl.acidcats.tumblrlikes.ui.screens.base

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import nl.acidcats.tumblrlikes.LikesApplication
import nl.acidcats.tumblrlikes.di.AppComponent

/**
 * Created on 29/10/2018.
 */
abstract class BaseFragment : Fragment(), BaseView {

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        injectFrom((activity?.application as LikesApplication).appComponent)
    }

    protected abstract fun injectFrom(appComponent: AppComponent)

    override fun sendBroadcast(action: String) {
        context?.let {
            LocalBroadcastManager.getInstance(it).sendBroadcast(Intent(action))
        }
    }

    override fun clearArgument(key: String) {
        arguments?.remove(key)
    }
}