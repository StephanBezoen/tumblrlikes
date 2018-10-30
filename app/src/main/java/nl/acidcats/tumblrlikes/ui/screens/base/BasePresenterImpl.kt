package nl.acidcats.tumblrlikes.ui.screens.base

import rx.Subscription
import rx.subscriptions.CompositeSubscription

/**
 * Created on 30/10/2018.
 */
open class BasePresenterImpl<V : BaseView> : BasePresenter<V> {

    private var view: V? = null
    private val compositeSubscription = CompositeSubscription()

    override fun setView(view: V) {
        this.view = view
    }

    protected fun getView(): V? = view

    override fun notify(action: String) {
        getView()?.sendBroadcast(action)
    }

    protected fun registerSubscription(subscription: Subscription) {
        compositeSubscription.add(subscription)
    }

    override fun onDestroyView() {
        compositeSubscription.unsubscribe()

        view = null
    }
}
