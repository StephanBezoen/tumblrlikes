package nl.acidcats.tumblrlikes

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.facebook.stetho.Stetho
import com.github.ajalt.timberkt.Timber
import com.github.ajalt.timberkt.Timber.DebugTree
import com.google.firebase.analytics.FirebaseAnalytics
import io.fabric.sdk.android.Fabric
import nl.acidcats.tumblrlikes.di.AppComponent
import nl.acidcats.tumblrlikes.di.AppModule
import nl.acidcats.tumblrlikes.di.DaggerAppComponent
import nl.acidcats.tumblrlikes.di.DataModule

/**
 * Created on 05/11/2018.
 */
class LikesApplication : Application() {
    lateinit var appComponent: AppComponent
        private set

    var isFreshRun = true
        private set

    override fun onCreate() {
        super.onCreate()

        Timber.plant(DebugTree())

        Fabric.with(this, Crashlytics())

        if (BuildConfig.DEBUG && BuildConfig.USE_STETHO) {
            Stetho.initializeWithDefaults(this)
        }

        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this, FirebaseAnalytics.getInstance(this)))
                .dataModule(DataModule(this))
                .build()
    }

    fun clearFreshRun() {
        isFreshRun = false
    }
}