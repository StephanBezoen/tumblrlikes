package nl.acidcats.tumblrlikes.data.services;

import android.app.Service;
import android.os.Binder;

/**
 * Created by stephan on 23/12/2016.
 */

public class ServiceBinder<T extends Service> extends Binder {
    private static final String TAG = ServiceBinder.class.getSimpleName();

    private T _service;

    public ServiceBinder(T service) {
        _service = service;
    }

    public T getService() {
        return _service;
    }
}
