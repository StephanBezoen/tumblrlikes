package nl.acidcats.tumblrlikes.util

import android.app.Service
import android.os.Binder

/**
 * Created by stephan on 23/12/2016.
 */

class ServiceBinder<T : Service>(val service: T) : Binder()
