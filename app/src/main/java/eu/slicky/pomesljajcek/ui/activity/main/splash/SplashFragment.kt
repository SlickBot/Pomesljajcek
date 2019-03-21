package eu.slicky.pomesljajcek.ui.activity.main.splash

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import eu.slicky.pomesljajcek.R
import eu.slicky.pomesljajcek.databinding.FragmentSplashBinding
import eu.slicky.pomesljajcek.ui.activity.BaseFragment
import eu.slicky.pomesljajcek.ui.activity.main.MainActivity
import eu.slicky.pomesljajcek.util.getVersionName

/**
 * https://medium.com/@andkulikov/animate-all-the-things-transitions-in-android-914af5477d50
 */
class SplashFragment : BaseFragment() {

    private lateinit var binding: FragmentSplashBinding

    /* OVERRIDE */

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_splash, container, false)
        binding.fragment = this

        binding.splashBtnContinue.setOnClickListener {
            val data = dataHandler ?: return@setOnClickListener
            if (data.pref.shouldShowPPDialog()) {
                showPrivacyPolicyDialog()
            } else {
                showYearFragment()
            }
        }

        binding.splashTvVersion.text = getString(R.string.splash_version, context?.getVersionName() ?: "?")

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        val activity = activity as MainActivity
        activity.changeBackground(1)
    }

    /* BODY */

    private fun showPrivacyPolicyDialog() {
        dialogs?.showAlertDialog {
            setTitle(R.string.dialog_pp_title)
            setMessage(R.string.dialog_pp_message)
            setPositiveButton(R.string.accept) { _, _ -> onAgreeToPPClick() }
            setNegativeButton(R.string.cancel) { _, _ -> }
        }?.apply {
            findViewById<TextView>(android.R.id.message)?.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    private fun onAgreeToPPClick() {
        dataHandler?.pref?.setShouldShowPPDialog(false)
        showYearFragment()
    }

//    private fun startRevealLogoAnimation() {
//        revealTransition(onEnd = {
//            startRevealNameAnimation()
//            startRevealBackgroundAnimation()
//        })
//        binding.splashLogo.visibility = View.VISIBLE
//    }
//
//    private fun startRevealNameAnimation() {
//        revealTransition(onEnd = {
//            startRevealQuestionsAnimation()
//        })
//        binding.splashText.visibility = View.VISIBLE
//    }
//
//    private fun startRevealBackgroundAnimation() {
//        val context = context ?: return
//        val colorFrom = ContextCompat.getColor(context, R.color.colorPrimaryDark)
//        val colorTo = ContextCompat.getColor(context, R.color.colorPrimary)
//        ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo).apply {
//            duration = 700
//            interpolator = DecelerateInterpolator()
//            addUpdateListener {
//                binding.splashRoot.setBackgroundColor(animatedValue as Int)
//            }
//        }.start()
//    }
//
//    private fun startRevealQuestionsAnimation() {
//        val animation = AlphaAnimation(1f, 0f).apply {
//            duration = 700
////                repeatCount = ValueAnimator.INFINITE
//            repeatMode = ValueAnimator.REVERSE
//            interpolator = AccelerateDecelerateInterpolator()
//            addListener(onEnd = {
//                openMainScreen()
//            })
//        }
//        binding.splashQuestions.startAnimation(animation)
//    }
//
//    private fun revealTransition(onEnd: (Transition) -> Unit) {
//        TransitionManager.beginDelayedTransition(
//            binding.splashRoot, TransitionSet()
//                .addTransition(Scale(0f).setDuration(300))
//                .addTransition(Fade().setDuration(350))
//                .addTransition(ChangeBounds().setDuration(300))
//                .addListener(onEnd = onEnd)
//        )
//    }
//
//    private fun hideTransition(onEnd: (Transition) -> Unit) {
//        TransitionManager.beginDelayedTransition(
//            binding.splashRoot, TransitionSet()
//                .addTransition(Scale(0f).setDuration(300))
//                .addTransition(Fade().setDuration(350))
//                .addTransition(ChangeBounds().setDuration(300))
//                .addListener(onEnd = onEnd)
//        )
//    }
//
//    private fun openMainScreen() {
//        view?.findNavController()?.navigate(
//            SplashFragmentDirections.ActionSplashFragmentToMainFragment(),
//            FragmentNavigatorExtras(
//                binding.splashLogo to getString(R.string.transition_logo),
//                binding.splashText to getString(R.string.transition_text)
//            )
//        )
//    }

    private fun showYearFragment() {
        view?.findNavController()?.navigate(
            SplashFragmentDirections.actionSplashFragmentToMainFragment(),
            FragmentNavigatorExtras(
                binding.splashTitle to getString(R.string.transition_title)
            )
        )
    }

}