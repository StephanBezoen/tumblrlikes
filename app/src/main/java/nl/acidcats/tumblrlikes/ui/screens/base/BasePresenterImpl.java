package nl.acidcats.tumblrlikes.ui.screens.base;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created on 18/10/2018.
 */
public class BasePresenterImpl<V> implements BasePresenter<V>  {

    private V _view;
    private CompositeSubscription _unsubscriber = new CompositeSubscription();

    @Override
    public void setView(V view) {
        _view = view;
    }

    public V getView() {
        return _view;
    }

    protected void registerSubscription(Subscription subscription) {
        _unsubscriber.add(subscription);
    }

    @Override
    public void onDestroyView() {
        _unsubscriber.unsubscribe();
    }
}
