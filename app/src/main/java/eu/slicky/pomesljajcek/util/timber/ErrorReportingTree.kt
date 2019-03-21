package eu.slicky.pomesljajcek.util.timber

import android.util.Log
import timber.log.Timber

class ErrorReportingTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, th: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG)
            return

//        Crashlytics.log(priority, tag, message)
//        throwable?.let { Crashlytics.logException(it) }
    }
}