package eu.slicky.pomesljajcek.ui.activity.main.entry

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
import eu.slicky.pomesljajcek.data.db.EntryEntity
import kotlinx.android.synthetic.main.item_entry.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*


class EntryAdapter(
    private val recycler: RecyclerView,
    private val onClick: (ViewHolder) -> Unit = {},
    private val onLongClick: (ViewHolder) -> Boolean = { false },
    private val onEditClick: (ViewHolder) -> Unit = {},
    private val onDeleteClick: (ViewHolder) -> Unit = {}
) : RecyclerSwipeAdapter<EntryAdapter.ViewHolder>() {

    private var items = emptyList<EntryEntity>()

    suspend fun setItems(entries: List<EntryEntity>, calcDiff: Boolean) = withContext(Dispatchers.Main) {
        if (calcDiff) {
            val result = withContext(Dispatchers.IO) { DiffUtil.calculateDiff(DiffCallback(items, entries)) }
            items = entries
            result.dispatchUpdatesTo(this@EntryAdapter)
        } else {
            items = entries
            notifyDataSetChanged()
        }
    }

    fun getItems() = items

    init {
        mode = Attributes.Mode.Single
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_entry, parent, false)
        return EntryAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: EntryAdapter.ViewHolder, position: Int) {
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

            character.text = item.character
            pinyin.text = item.pinyin
            simplified.text = if (item.hasTraditional) recycler.context.getString(R.string.has_traditional) else ""

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

    fun findItemFor(viewHolder: RecyclerView.ViewHolder): EntryEntity {
        val itemPosition = recycler.getChildLayoutPosition(viewHolder.itemView)
        return items[itemPosition]
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val swipeRoot: SwipeLayout = view.swipe_root

        val swipeMainContainer: View = view.swipe_main_container
        val character: TextView = view.character
        val pinyin: TextView = view.pinyin
        val simplified: TextView = view.simplified

        val swipeLeftContainer: View = view.swipe_left_container
        val deleteButton: Button = view.delete_button

        val swipeRightContainer: View = view.swipe_right_container
        val editButton: View = view.edit_button
    }

    inner class DiffCallback(
        private var old: List<EntryEntity>,
        private var new: List<EntryEntity>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = old.size
        override fun getNewListSize() = new.size
        override fun areItemsTheSame(oldPosition: Int, newPosition: Int) = old[oldPosition].id == new[newPosition].id
        override fun areContentsTheSame(oldPosition: Int, newPosition: Int) = old[oldPosition] == new[newPosition]
    }

}
