package eu.slicky.pomesljajcek.ui.activity.quiz

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import eu.slicky.pomesljajcek.R
import eu.slicky.pomesljajcek.databinding.FragmentQuizFinalBinding
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

class QuizFinalFragment : Fragment() {

    companion object {
        fun newInstance() = QuizFinalFragment()
    }

    private lateinit var binding: FragmentQuizFinalBinding
    private lateinit var task: TimerTask

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_quiz_final, container, false)
        binding.fragment = this

        task = Timer().scheduleAtFixedRate(0, 1000) {
            val activity = activity as? QuizActivity ?: return@scheduleAtFixedRate
            val answeredCount = activity.quizAdapter.questions.count { it.answered }
            Handler(Looper.getMainLooper()).post {
                binding.answeredText.text = getString(R.string.quiz_result_text, answeredCount)
            }
        }

//        binding.valueText.text = question.value
//        binding.typeText.text = if (question.simplified) "SIMPLIFIED" else "TRADITIONAL"
//        binding.answer.text = question.answer

//        if (question.answered) {
//            binding.answer.visibility = View.VISIBLE
//            binding.showAnswer.visibility = View.GONE
//        }
//        binding.answerLayout.setOnClickListener {
//            question.answered = true
//            setAnswerVisibility(binding.answer.visibility != View.VISIBLE)
//        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::task.isInitialized) {
            task.cancel()
        }
    }

}
