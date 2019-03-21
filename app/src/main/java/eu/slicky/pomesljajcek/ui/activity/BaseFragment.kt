package eu.slicky.pomesljajcek.ui.activity

import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.TransitionSet
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import eu.slicky.pomesljajcek.data.DataHandler
import eu.slicky.pomesljajcek.util.transition.TextEnterSharedElementCallback
import eu.slicky.pomesljajcek.util.transition.TextSizeTransition

//@Suppress("unused")
abstract class BaseFragment : Fragment() {

    val mainScope = LifecycleScope()

    val baseActivity: BaseActivity? get() = activity as? BaseActivity
    val dataHandler: DataHandler? get() = baseActivity?.dataHandler

    val dialogs: Dialogs? get() = context?.let { Dialogs(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(mainScope)
    }

    fun addSharedTextElementTransition() {
        sharedElementEnterTransition = TransitionSet().apply {
            ordering = TransitionSet.ORDERING_TOGETHER
            duration = 300
            addTransition(ChangeBounds())
            addTransition(TextSizeTransition())
        }
        setEnterSharedElementCallback(TextEnterSharedElementCallback())
    }

    fun navigateBack() {
        view?.findNavController()?.popBackStack()
    }

}
