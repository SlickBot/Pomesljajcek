package eu.slicky.pomesljajcek.ui.activity.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.transition.TransitionManager
import eu.slicky.pomesljajcek.R
import eu.slicky.pomesljajcek.databinding.FragmentQuizQuestionBinding
import eu.slicky.pomesljajcek.model.Question

class QuizQuestionFragment : Fragment() {

    companion object {
        private const val QUESTION_EXTRA = "question_extra"

        fun newInstance(question: Question) = QuizQuestionFragment().apply {
            arguments = Bundle().apply {
                putParcelable(QUESTION_EXTRA, question)
            }
        }
    }

    private lateinit var binding: FragmentQuizQuestionBinding
    private lateinit var question: Question

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        question = arguments!!.getParcelable(QUESTION_EXTRA)!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_quiz_question, container, false)
        binding.fragment = this

        binding.valueText.text = question.value
        binding.typeText.text = if (question.hasTraditional) getString(R.string.has_traditional) else ""
        binding.answer.text = question.answer

//        if (question.answered) {
//            binding.answer.visibility = View.VISIBLE
//            binding.showAnswer.visibility = View.GONE
//        }
        binding.answerLayout.setOnClickListener {
            question.answered = true
            setAnswerVisibility(binding.answer.visibility != View.VISIBLE)
        }

        return binding.root
    }

    private fun setAnswerVisibility(visible: Boolean) {
        TransitionManager.beginDelayedTransition(binding.container)
        if (visible) {
            binding.showAnswer.visibility = View.GONE
            binding.answer.visibility = View.VISIBLE
        } else {
            binding.showAnswer.visibility = View.VISIBLE
            binding.answer.visibility = View.GONE
        }
    }

}