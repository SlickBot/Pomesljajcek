package eu.slicky.pomesljajcek.ui.activity.quiz

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.databinding.DataBindingUtil
import eu.slicky.pomesljajcek.R
import eu.slicky.pomesljajcek.databinding.ActivityQuizBinding
import eu.slicky.pomesljajcek.ui.activity.BaseActivity

class QuizActivity : BaseActivity() {

    private lateinit var binding: ActivityQuizBinding

    private lateinit var alphaAnimation: ValueAnimator
    private lateinit var scaleAnimation: ValueAnimator

    lateinit var quizAdapter: QuizAdapter

    /* OVERRIDE */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quiz)

        alphaAnimation = ValueAnimator.ofFloat(1f, 0.5f).apply {
            addUpdateListener {
                binding.backgroundImage.alpha = it.animatedValue as Float
            }
            duration = 21000
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }

        scaleAnimation = ValueAnimator.ofFloat(1f, 1.5f).apply {
            addUpdateListener {
                binding.backgroundImage.scaleX = it.animatedValue as Float
                binding.backgroundImage.scaleY = it.animatedValue as Float
            }
            duration = 30000
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
    }

    override fun onStart() {
        super.onStart()
        alphaAnimation.start()
        scaleAnimation.start()
    }

    override fun onStop() {
        super.onStop()
        alphaAnimation.cancel()
        scaleAnimation.cancel()
    }

}
