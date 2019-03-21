package eu.slicky.pomesljajcek.ui.activity.main.year

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
import eu.slicky.pomesljajcek.data.db.YearEntity
import eu.slicky.pomesljajcek.databinding.FragmentYearBinding
import eu.slicky.pomesljajcek.ui.activity.BaseFragment
import eu.slicky.pomesljajcek.ui.activity.main.MainActivity
import eu.slicky.pomesljajcek.util.addItemTouchHelper
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import kotlinx.android.synthetic.main.dialog_year.view.*
import kotlinx.coroutines.launch

class YearFragment : BaseFragment() {

    private lateinit var binding: FragmentYearBinding
    private lateinit var yearAdapter: YearAdapter

    /* OVERRIDE */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addSharedTextElementTransition()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_year, container, false)
        binding.fragment = this
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
        activity.changeBackground(2)
    }

    /* INIT */

    private fun initListeners() {
        binding.navLeftButton.setOnClickListener { }
//        binding.navRightButton.setOnClickListener { }
//        binding.fabLeft.setOnClickListener { openChallengeDialog() }
        binding.fabRight.setOnClickListener { openNewDialog() }
    }

    private fun initRecycler() {
        with(binding.recycler) {
            layoutManager = LinearLayoutManager(context)

            adapter = YearAdapter(
                binding.recycler,
                onClick = { showSectionFragment(yearAdapter.findItemFor(it), it.title) },
                onLongClick = { false },
                onEditClick = { openEditDialog(yearAdapter.findItemFor(it)) },
                onDeleteClick = { openDeleteDialog(yearAdapter.findItemFor(it)) }
            ).also {
                yearAdapter = it
            }.let {
                AlphaInAnimationAdapter(it)
            }

            addItemTouchHelper(
                onDrag = { from, to ->
                    yearAdapter.swap(from, to)
                    saveYearOrder()
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
        yearAdapter.mItemManger.closeAllItems()
        yearAdapter.setItems(data.getYears(), calcDiff)
    }


    private fun addYear(yearName: String) = mainScope.launch {
        val data = dataHandler ?: return@launch
        data.addYear(yearName)
        updateItems()
    }

    private fun editYear(year: YearEntity, yearName: String) = mainScope.launch {
        val data = dataHandler ?: return@launch
        data.updateYear(year.copy(name = yearName))
        updateItems()
    }

    private fun deleteYear(year: YearEntity) = mainScope.launch {
        val data = dataHandler ?: return@launch
        data.deleteYear(year)
        updateItems()
    }

    private var fabVisible = true
    private fun animateFabVisibility(visible: Boolean) {
        if (!visible && fabVisible) {
            TransitionManager.beginDelayedTransition(binding.fabContainer)
//            binding.fabLeft.visibility = View.GONE
            binding.fabRight.visibility = View.GONE
            fabVisible = false
        }
        else if (visible && !fabVisible) {
            TransitionManager.beginDelayedTransition(binding.fabContainer)
//            binding.fabLeft.visibility = View.VISIBLE
            binding.fabRight.visibility = View.VISIBLE
            fabVisible = true
        }
    }

    private fun saveYearOrder() {
        mainScope.launch {
            dataHandler?.saveYearOrder(yearAdapter.getItems())
        }
    }

    /* DIALOG */

    private fun openNewDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_year, binding.container, false)
        val nameField = view.year_name

        dialogs?.showSaveCancel(
            titleId = R.string.add_year_title,
            view = view,
            cancelable = true,
            onSave = { addYear(nameField.text.toString()) },
            onCancel = { yearAdapter.closeAllItems() }
        )?.apply {
            window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            nameField.setSelection(nameField.text.length)
        }
    }

    private fun openEditDialog(year: YearEntity) {
        val view = layoutInflater.inflate(R.layout.dialog_year, binding.container, false)
        val nameField = view.year_name
        nameField.setText(year.name)

        dialogs?.showEditCancel(
            titleId = R.string.edit_year_title,
            view = view,
            cancelable = true,
            onSave = { editYear(year, nameField.text.toString()) },
            onCancel = { yearAdapter.closeAllItems() }
        )?.apply {
            window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            nameField.setSelection(nameField.text.length)
        }
    }

    private fun openDeleteDialog(year: YearEntity) {
        dialogs?.showSimpleYesCancel(
            title = getString(R.string.delete_year_title),
            message = getString(R.string.delete_year_message, year.name),
            iconId = R.drawable.ic_warning,
            iconTintId = R.color.colorError,
            cancelable = true,
            onYes = { deleteYear(year) },
            onCancel = { yearAdapter.closeAllItems() }
        )
    }

    /* NAVIGATION */

    private fun showSectionFragment(year: YearEntity, title: View) {
        view?.findNavController()?.navigate(
            YearFragmentDirections.actionYearFragmentToSectionFragment(year, title.transitionName),
            FragmentNavigatorExtras(
                binding.title to getString(R.string.transition_title),
                title to title.transitionName
            )
        )
    }

}