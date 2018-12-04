package nl.acidcats.tumblrlikes.ui.screens.base

import androidx.lifecycle.ViewModel
import rx.Subscription
import rx.subscriptions.CompositeSubscription

/**
 * Created on 04/12/2018.
 */
open class BaseViewModel : ViewModel() {
    private val compositeSubscription = CompositeSubscription()


    protected fun registerSubscription(subscription: Subscription) {
        compositeSubscription.add(subscription)
    }

    override fun onCleared() {
        super.onCleared()

        compositeSubscription.unsubscribe()
    }
}