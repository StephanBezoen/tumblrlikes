package nl.acidcats.tumblrlikes.di.component

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import nl.acidcats.tumblrlikes.LikesApplication
import nl.acidcats.tumblrlikes.di.DataModule
import nl.acidcats.tumblrlikes.di.module.*
import javax.inject.Singleton

/**
 * Created by stephan on 28/03/2017.
 */

@Singleton
@Component(modules = [
    AppModule::class,
    ActivityModule::class,
    ServiceModule::class,
    DataModule::class,
    AndroidInjectionModule::class,
    PresenterModule::class,
    ViewModelModule::class
])
interface AppComponent {
    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        @BindsInstance
        fun analytics(analytics: FirebaseAnalytics): Builder

        fun build(): AppComponent
    }

    fun inject(likesApplication: LikesApplication)
}
