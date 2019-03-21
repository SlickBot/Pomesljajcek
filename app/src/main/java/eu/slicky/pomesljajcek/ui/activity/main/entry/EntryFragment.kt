package eu.slicky.pomesljajcek.ui.activity.main.entry

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import eu.slicky.pomesljajcek.R
import eu.slicky.pomesljajcek.data.db.EntryEntity
import eu.slicky.pomesljajcek.data.db.SectionEntity
import eu.slicky.pomesljajcek.databinding.FragmentEntryBinding
import eu.slicky.pomesljajcek.ui.activity.BaseFragment
import eu.slicky.pomesljajcek.ui.activity.main.MainActivity
import eu.slicky.pomesljajcek.ui.activity.quiz.QuizActivity
import eu.slicky.pomesljajcek.util.addItemTouchHelper
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import kotlinx.android.synthetic.main.dialog_entry.view.*
import kotlinx.coroutines.launch
import java.util.*

class EntryFragment : BaseFragment() {

    private lateinit var binding: FragmentEntryBinding
    private lateinit var entryAdapter: EntryAdapter

    private lateinit var section: SectionEntity

    private lateinit var tts: TextToSpeech

    /* OVERRIDE */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addSharedTextElementTransition()
        initArguments()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_entry, container, false)
        binding.fragment = this
        binding.title.text = section.name
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
        activity.changeBackground(0)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
    }

    /* INIT */

    private fun initArguments() {
        section = arguments?.getParcelable("section")
            ?: throw IllegalArgumentException("section is not in arguments")
    }

    private fun initListeners() {
        binding.navLeftButton.setOnClickListener { navigateBack() }
//        binding.navRightButton.setOnClickListener { openNewDialog() }
        binding.fabLeft.setOnClickListener { startQuiz() }
        binding.fabRight.setOnClickListener { openNewDialog() }
    }

    private fun initRecycler() {
        with(binding.recycler) {
            layoutManager = GridLayoutManager(context, 2)
//            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

            adapter = EntryAdapter(
                binding.recycler,
                onClick = { playTts(entryAdapter.findItemFor(it)) },
                onLongClick = { false },
                onEditClick = { openEditDialog(entryAdapter.findItemFor(it)) },
                onDeleteClick = { openDeleteDialog(entryAdapter.findItemFor(it)) }
            ).also {
                entryAdapter = it
            }.let {
                AlphaInAnimationAdapter(it)
            }

            addItemTouchHelper(
                onDrag = { from, to ->
                    entryAdapter.swap(from, to)
                    saveEntityOrder()
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
        entryAdapter.mItemManger.closeAllItems()
        entryAdapter.setItems(data.getEntries(section), calcDiff)
    }

    private fun addEntry(pinyin: String, character: String, simplified: Boolean) = mainScope.launch {
        val data = dataHandler ?: return@launch
        data.addEntry(section, character, pinyin, simplified)
        updateItems()
    }

    private fun editEntry(entry: EntryEntity, pinyin: String, character: String, hasTraditional: Boolean) = mainScope.launch {
        val data = dataHandler ?: return@launch
        data.updateEntry(entry.copy(pinyin = pinyin, character = character, hasTraditional = hasTraditional))
        updateItems()
    }

    private fun deleteEntry(entry: EntryEntity) = mainScope.launch {
        val data = dataHandler ?: return@launch
        data.deleteEntry(entry)
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

    private fun playTts(entry: EntryEntity) {
        val listener = TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = if (entry.hasTraditional)
                    Locale.SIMPLIFIED_CHINESE
                else
                    Locale.TRADITIONAL_CHINESE
                tts.speak(entry.character, TextToSpeech.QUEUE_ADD, Bundle.EMPTY, "entry_tts")
            }
        }
        tts = TextToSpeech(context, listener)
    }

    private fun saveEntityOrder() {
        mainScope.launch {
            dataHandler?.saveEntryOrder(section, entryAdapter.getItems())
        }
    }

    /* DIALOG */

    private fun openNewDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_entry, binding.container, false)
        val pinyinField = view.entry_pinyin
        val characterField = view.entry_character
        val simplifiedButton = view.entry_has_traditional

        dialogs?.showSaveCancel(
            titleId = R.string.add_entry_title,
            view = view,
            cancelable = true,
            onSave = { addEntry(pinyinField.text.toString(), characterField.text.toString(), simplifiedButton.isChecked) },
            onCancel = { entryAdapter.closeAllItems() }
        )?.apply {
            window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            pinyinField.setSelection(pinyinField.text.length)
        }
    }

    private fun openEditDialog(entry: EntryEntity) {
        val view = layoutInflater.inflate(R.layout.dialog_entry, binding.container, false)
        val pinyinField = view.entry_pinyin
        pinyinField.setText(entry.pinyin)
        val characterField = view.entry_character
        characterField.setText(entry.character)
        val hasTraditionalButton = view.entry_has_traditional
        hasTraditionalButton.isChecked = entry.hasTraditional

        dialogs?.showSaveCancel(
            titleId = R.string.edit_entry_title,
            view = view,
            cancelable = true,
            onSave = { editEntry(entry, pinyinField.text.toString(), characterField.text.toString(), hasTraditionalButton.isChecked) },
            onCancel = { entryAdapter.closeAllItems() }
        )?.apply {
            window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            pinyinField.setSelection(pinyinField.text.length)
        }
    }

    private fun openDeleteDialog(entry: EntryEntity) {
        dialogs?.showSimpleYesCancel(
            title = getString(R.string.delete_entry_title),
            message = getString(R.string.delete_entry_message, entry.pinyin),
            iconId = R.drawable.ic_warning,
            iconTintId = R.color.colorError,
            cancelable = true,
            onYes = { deleteEntry(entry) },
            onCancel = { entryAdapter.closeAllItems() }
        )
    }

    /* NAVIGATION */

//    private fun showEntityFragment(yearNumber: old_sections) {
//        view?.findNavController()?.navigate(
//            SectionFragmentDirections.ActionSectionFragmentToEntityFragment(yearNumber),
//            FragmentNavigatorExtras(
//                binding.title to getString(R.string.transition_title)
//            )
//        )
//    }

    private fun startQuiz() {
        val intent = Intent(context, QuizActivity::class.java)
        intent.putExtra("section_ids", longArrayOf(section.id))
        startActivity(intent)
    }

}
