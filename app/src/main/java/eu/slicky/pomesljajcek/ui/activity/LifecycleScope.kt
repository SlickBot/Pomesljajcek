package eu.slicky.pomesljajcek.ui.activity

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class LifecycleScope : CoroutineScope, LifecycleObserver {

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    @Suppress("unused")
    fun create() {
        job = Job()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    @Suppress("unused")
    fun cancel() {
        job.cancel()
    }

}
