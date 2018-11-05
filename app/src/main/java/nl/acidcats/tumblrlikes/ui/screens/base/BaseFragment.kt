package nl.acidcats.tumblrlikes.ui.screens.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.view.View
import butterknife.ButterKnife
import butterknife.Unbinder
import nl.acidcats.tumblrlikes.LikesApplication
import nl.acidcats.tumblrlikes.di.AppComponent
import rx.subscriptions.CompositeSubscription

/**
 * Created on 29/10/2018.
 */
abstract class BaseFragment : Fragment(), BaseView {

    private val unsubscriber: CompositeSubscription = CompositeSubscription()
    private lateinit var unbinder: Unbinder

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        injectFrom((activity?.application as LikesApplication).appComponent)
    }

    protected abstract fun injectFrom(appComponent: AppComponent)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        unbinder = ButterKnife.bind(this, view)
    }

    override fun sendBroadcast(action: String) {
        context?.let {
            LocalBroadcastManager.getInstance(it).sendBroadcast(Intent(action))
        }
    }

    override fun onDestroyView() {
        unbinder.unbind()

        unsubscriber.unsubscribe()

        super.onDestroyView()
    }
}