package nl.acidcats.tumblrlikes

import android.app.Activity
import android.app.Application
import android.app.Service
import com.crashlytics.android.Crashlytics
import com.facebook.stetho.Stetho
import com.github.ajalt.timberkt.Timber
import com.github.ajalt.timberkt.Timber.DebugTree
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasServiceInjector
import io.fabric.sdk.android.Fabric
import nl.acidcats.tumblrlikes.di.AppInjector
import javax.inject.Inject

/**
 * Created on 05/11/2018.
 */
class LikesApplication : Application(), HasActivityInjector, HasServiceInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>
    @Inject
    lateinit var serviceInjector: DispatchingAndroidInjector<Service>

    var isFreshRun = true
        private set

    override fun onCreate() {
        super.onCreate()

        initDagger()
        initTimber()
        initFabric()
        initStetho()
    }

    private fun initDagger() {
        AppInjector.init(this)
    }

    private fun initStetho() {
        if (BuildConfig.DEBUG && BuildConfig.USE_STETHO) {
            Stetho.initializeWithDefaults(this)
        }
    }

    private fun initFabric() {
//        Fabric.with(this, Crashlytics())
    }

    private fun initTimber() {
        Timber.plant(DebugTree())
    }

    fun clearFreshRun() {
        isFreshRun = false
    }

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector

    override fun serviceInjector(): AndroidInjector<Service> = serviceInjector
}
