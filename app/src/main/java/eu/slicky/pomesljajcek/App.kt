package eu.slicky.pomesljajcek

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.facebook.stetho.Stetho
import eu.slicky.pomesljajcek.data.DataHandler
import eu.slicky.pomesljajcek.util.timber.ErrorReportingTree
import io.fabric.sdk.android.Fabric
import timber.log.Timber

class App : Application() {

    companion object {
        lateinit var instance: App private set
    }

    val dataHandler: DataHandler by lazy { DataHandler(this) }

    override fun onCreate() {
        super.onCreate()
        instance = this
        initialize()
    }

    private fun initialize() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())

            // http://facebook.github.io/stetho/
            Stetho.initializeWithDefaults(this)

            // https://github.com/square/leakcanary
//            if (!LeakCanary.isInAnalyzerProcess(this))
//                LeakCanary.install(this)

            // https://developer.android.com/reference/android/os/StrictMode.html
//            StrictMode.enableDefaults()
        } else {
            Timber.plant(ErrorReportingTree())
            // https://medium.com/fuzz/getting-the-most-out-of-crashlytics-380afb703876
             Fabric.with(this, Crashlytics())
        }
    }

}
