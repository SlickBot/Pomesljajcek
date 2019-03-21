package eu.slicky.pomesljajcek.ui.activity.main.section

import android.content.Intent
import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import eu.slicky.pomesljajcek.R
import eu.slicky.pomesljajcek.data.db.SectionEntity
import eu.slicky.pomesljajcek.data.db.YearEntity
import eu.slicky.pomesljajcek.databinding.FragmentSectionBinding
import eu.slicky.pomesljajcek.ui.activity.BaseFragment
import eu.slicky.pomesljajcek.ui.activity.main.MainActivity
import eu.slicky.pomesljajcek.ui.activity.quiz.QuizActivity
import eu.slicky.pomesljajcek.util.addItemTouchHelper
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import kotlinx.android.synthetic.main.dialog_section.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SectionFragment : BaseFragment() {

    private lateinit var binding: FragmentSectionBinding
    private lateinit var sectionAdapter: SectionAdapter

    private lateinit var year: YearEntity

    /* OVERRIDE */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addSharedTextElementTransition()
        initArguments()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_section, container, false)
        binding.fragment = this
        binding.title.text = year.name
        initListeners()
        initRecycler()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        updateItems(false)
        // to update UI soft key state
        ViewCompat.requestApplyInsets(binding.container)
    }

    override fun onResume() {
        super.onResume()

        val activity = activity as MainActivity
        activity.changeBackground(3)
    }

    /* INIT */

    private fun initArguments() {
        year = arguments?.getParcelable("year")
            ?: throw IllegalArgumentException("year is not in arguments")
    }

    private fun initListeners() {
        binding.navLeftButton.setOnClickListener { navigateBack() }
//        binding.navRightButton.setOnClickListener { }
        binding.fabLeft.setOnClickListener { openChallengeDialog() }
        binding.fabRight.setOnClickListener { openNewDialog() }
    }

    private fun initRecycler() {
        with(binding.recycler) {
            layoutManager = LinearLayoutManager(context)

            adapter = SectionAdapter(
                binding.recycler,
                onClick = { showEntityFragment(sectionAdapter.findItemFor(it)) },
                onLongClick = { false },
                onPlayClick = { startQuiz(sectionAdapter.findItemFor(it)) },
                onEditClick = { openEditDialog(sectionAdapter.findItemFor(it)) },
                onDeleteClick = { openDeleteDialog(sectionAdapter.findItemFor(it)) }
            ).also {
                sectionAdapter = it
            }.let {
                AlphaInAnimationAdapter(it)
            }

            addItemTouchHelper(
                onDrag = { from, to ->
                    sectionAdapter.swap(from, to)
                    saveSectionOrder()
                    true
                }
            )

            addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val y = recyclerView.computeVerticalScrollOffset()
                    animateFabVisibility(y < 10 || dy < 0)
                }
            })
        }
    }

    /* BODY */

    private fun updateItems(calcDiff: Boolean = true) = mainScope.launch {
        val data = dataHandler ?: return@launch
        sectionAdapter.mItemManger.closeAllItems()
        sectionAdapter.setItems(data.getSections(year), calcDiff)
    }

    private fun addSection(sectionName: String) = mainScope.launch {
        val data = dataHandler ?: return@launch
        data.addSection(year, sectionName)
        updateItems()
    }

    private fun editSection(section: SectionEntity, sectionName: String) = mainScope.launch {
        val data = dataHandler ?: return@launch
        data.updateSection(section.copy(name = sectionName))
        updateItems()
    }

    private fun deleteSection(section: SectionEntity) = mainScope.launch {
        val data = dataHandler ?: return@launch
        data.deleteSection(section)
        updateItems()
    }

    private var fabVisible = true
    private fun animateFabVisibility(visible: Boolean) {
        if (!visible && fabVisible) {
            TransitionManager.beginDelayedTransition(binding.fabContainer)
            binding.fabLeft.visibility = View.GONE
            binding.fabRight.visibility = View.GONE
            fabVisible = false
        }
        else if (visible && !fabVisible) {
            TransitionManager.beginDelayedTransition(binding.fabContainer)
            binding.fabLeft.visibility = View.VISIBLE
            binding.fabRight.visibility = View.VISIBLE
            fabVisible = true
        }
    }

    private fun saveSectionOrder() {
        mainScope.launch {
            dataHandler?.saveSectionOrder(year, sectionAdapter.getItems())
        }
    }

    /* DIALOG */

    private fun openNewDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_section, binding.container, false)
        val nameField = view.section_name

        dialogs?.showSaveCancel(
            titleId = R.string.add_section_title,
            view = view,
            cancelable = true,
            onSave = { addSection(nameField.text.toString()) },
            onCancel = { sectionAdapter.closeAllItems() }
        )?.apply {
            window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            nameField.setSelection(nameField.text.length)
        }
    }

    private fun openEditDialog(section: SectionEntity) {
        val view = layoutInflater.inflate(R.layout.dialog_section, binding.container, false)
        val nameField = view.section_name
        nameField.setText(section.name)

        dialogs?.showEditCancel(
            titleId = R.string.edit_section_title,
            view = view,
            cancelable = true,
            onSave = { editSection(section, nameField.text.toString()) },
            onCancel = { sectionAdapter.closeAllItems() }
        )?.apply {
            window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            nameField.setSelection(nameField.text.length)
        }
    }

    private fun openDeleteDialog(section: SectionEntity) {
        dialogs?.showSimpleYesCancel(
            title = getString(R.string.delete_section_title),
            message = getString(R.string.delete_section_message, section.name),
            iconId = R.drawable.ic_warning,
            iconTintId = R.color.colorError,
            cancelable = true,
            onYes = { deleteSection(section) },
            onCancel = { sectionAdapter.closeAllItems() }
        )
    }

    private fun openChallengeDialog() = mainScope.launch {
        val data = dataHandler ?: return@launch

        val sections = data.getSections(year)
        val (texts, checks) = data.getChallengeData(year, sections)

        dialogs?.showListOkCancel(
            textList = texts,
            checkedList = checks,
            title = getString(R.string.challenge_dialog_title),
            cancelable = true,
            onYes = { indices ->
                mainScope.launch {
                    if (indices.isNotEmpty()) {
                        val sectionIds = withContext(Dispatchers.IO) { indices.map { sections[it].id } }
                        startChallenge(sectionIds)
                        data.saveChallengeCheckedIds(year, sectionIds)
                    }
                }
            },
            onCancel = { sectionAdapter.closeAllItems() }
        )
    }

    /* NAVIGATION */

    private fun showEntityFragment(section: SectionEntity) {
        view?.findNavController()?.navigate(
            SectionFragmentDirections.actionSectionFragmentToEntryFragment(section),
            FragmentNavigatorExtras(
                binding.title to getString(R.string.transition_title)
            )
        )
    }

    private fun startQuiz(section: SectionEntity) {
        val intent = Intent(context, QuizActivity::class.java)
        intent.putExtra("section_ids", longArrayOf(section.id))
        startActivity(intent)
    }

    private fun startChallenge(sectionIds: List<Long>) {
        val intent = Intent(context, QuizActivity::class.java)
        intent.putExtra("section_ids", sectionIds.toLongArray())
        startActivity(intent)
    }

}