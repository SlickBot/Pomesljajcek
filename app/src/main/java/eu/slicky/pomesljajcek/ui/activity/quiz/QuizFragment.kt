package eu.slicky.pomesljajcek.ui.activity.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.transition.TransitionManager
import androidx.viewpager.widget.ViewPager
import eu.slicky.pomesljajcek.R
import eu.slicky.pomesljajcek.databinding.FragmentQuizBinding
import eu.slicky.pomesljajcek.model.Question
import eu.slicky.pomesljajcek.ui.activity.BaseFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.min

class QuizFragment : BaseFragment() {

    private lateinit var binding: FragmentQuizBinding

    private lateinit var sectionIds: LongArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initArguments()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_quiz, container, false)

        val activity = activity as QuizActivity
        activity.quizAdapter = QuizAdapter(activity.supportFragmentManager)

        binding.viewPager.offscreenPageLimit = 0
        binding.viewPager.adapter = activity.quizAdapter
        binding.viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                binding.valueLeft.text = min(position + 1, activity.quizAdapter.questions.size).toString()
                setCounterVisibility(position >= activity.quizAdapter.questions.size)
            }
        })

        loadSections()

        return binding.root
    }

    /* INIT */

    private fun initArguments() {
        sectionIds = activity?.intent?.getLongArrayExtra("section_ids")
            ?: throw IllegalArgumentException("section_ids not in arguments")
    }

    private fun loadSections() = mainScope.launch {
        val data = dataHandler ?: return@launch
        val activity = activity as? QuizActivity ?: return@launch

        val questions = withContext(Dispatchers.IO) {
            mutableListOf<Question>().apply {
                sectionIds.forEach { id ->
                    data.getEntries(data.getSection(id)).forEach {
                        add(Question(it.character, it.pinyin, it.hasTraditional))
                        add(Question(it.pinyin, it.character, it.hasTraditional))
                    }
                }
            }.apply { shuffle() }.toList()
        }

        activity.quizAdapter.questions = questions
        binding.valueLeft.text = 1.toString()
        binding.valueRight.text = activity.quizAdapter.questions.size.toString()
    }

    private fun setCounterVisibility(visible: Boolean) {
        TransitionManager.beginDelayedTransition(binding.container)
        if (visible) {
            binding.valueLeft.visibility = View.GONE
            binding.valueRight.visibility = View.GONE
            binding.valueDivider.visibility = View.GONE
        } else {
            binding.valueLeft.visibility = View.VISIBLE
            binding.valueRight.visibility = View.VISIBLE
            binding.valueDivider.visibility = View.VISIBLE
        }
    }

}
