package eu.slicky.pomesljajcek.ui.activity.quiz

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import eu.slicky.pomesljajcek.model.Question
import kotlin.math.min

class QuizAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    var questions = emptyList<Question>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getCount() = if (questions.isNotEmpty()) questions.size + 1 else 0

    override fun getItem(position: Int) = if (position < questions.size)
        QuizQuestionFragment.newInstance(questions[position])
    else
        QuizFinalFragment.newInstance()

    override fun getPageTitle(position: Int) = "${min(position + 1, questions.size)} / ${questions.size}"

    

}