package eu.slicky.pomesljajcek.ui.activity.main.year

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.daimajia.swipe.SimpleSwipeListener
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import com.daimajia.swipe.util.Attributes
import eu.slicky.pomesljajcek.R
import eu.slicky.pomesljajcek.data.db.YearEntity
import kotlinx.android.synthetic.main.item_year.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*


class YearAdapter(
    private val recycler: RecyclerView,
    private val onClick: (ViewHolder) -> Unit = {},
    private val onLongClick: (ViewHolder) -> Boolean = { false },
    private val onEditClick: (ViewHolder) -> Unit = {},
    private val onDeleteClick: (ViewHolder) -> Unit = {}
) : RecyclerSwipeAdapter<YearAdapter.ViewHolder>() {

    private var items = emptyList<YearEntity>()

    init {
        mode = Attributes.Mode.Single
        setHasStableIds(true)
    }

    suspend fun setItems(years: List<YearEntity>, calcDiff: Boolean) = withContext(Dispatchers.Main) {
        if (calcDiff) {
            val result = withContext(Dispatchers.IO) { DiffUtil.calculateDiff(DiffCallback(items, years)) }
            items = years
            result.dispatchUpdatesTo(this@YearAdapter)
        } else {
            items = years
            notifyDataSetChanged()
        }
    }

    fun getItems() = items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_year, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        with(holder) {
            swipeRoot.showMode = SwipeLayout.ShowMode.PullOut
            swipeRoot.addDrag(SwipeLayout.DragEdge.Left, swipeLeftContainer)
            swipeRoot.addDrag(SwipeLayout.DragEdge.Right, swipeRightContainer)
            swipeRoot.addSwipeListener(object : SimpleSwipeListener() {
                override fun onOpen(layout: SwipeLayout) {
                    YoYo.with(Techniques.Tada)
                        .duration(500)
                        .delay(100)
                        .playOn(deleteButton)
                }
            })

            swipeMainContainer.setOnClickListener { onClick(holder) }
            swipeMainContainer.setOnLongClickListener { onLongClick(holder) }
            editButton.setOnClickListener { onEditClick(holder) }
            deleteButton.setOnClickListener { onDeleteClick(holder) }

            title.text = item.name
            title.transitionName = recycler.context.getString(R.string.transition_year_title)

            mItemManger.bindView(itemView, position)
        }
    }

    override fun getItemCount() = items.size

    override fun getItemId(position: Int) = items[position].id

    override fun getSwipeLayoutResourceId(position: Int) = R.id.swipe_root

    fun swap(from: RecyclerView.ViewHolder, to: RecyclerView.ViewHolder) {
        val fromPosition = from.adapterPosition
        val toPosition = to.adapterPosition

        if (fromPosition == toPosition)
            return

        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(items, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(items, i, i - 1)
            }
        }

        notifyItemMoved(fromPosition, toPosition)
    }

    fun findItemFor(viewHolder: RecyclerView.ViewHolder): YearEntity {
        val itemPosition = recycler.getChildLayoutPosition(viewHolder.itemView)
        return items[itemPosition]
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val swipeRoot: SwipeLayout = view.swipe_root

        val swipeMainContainer: View = view.swipe_main_container
        val title: TextView = view.title

        val swipeLeftContainer: View = view.swipe_left_container
        val deleteButton: Button = view.delete_button

        val swipeRightContainer: View = view.swipe_right_container
        val editButton: View = view.edit_button
    }

    inner class DiffCallback(
        private var old: List<YearEntity>,
        private var new: List<YearEntity>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = old.size
        override fun getNewListSize() = new.size
        override fun areItemsTheSame(oldPosition: Int, newPosition: Int) = old[oldPosition].id == new[newPosition].id
        override fun areContentsTheSame(oldPosition: Int, newPosition: Int) = old[oldPosition] == new[newPosition]
    }

}
