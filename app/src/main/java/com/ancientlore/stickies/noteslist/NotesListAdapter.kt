package com.ancientlore.stickies.noteslist

import android.content.Context
import android.support.annotation.UiThread
import android.support.v7.util.DiffUtil
import android.view.View
import android.widget.TextView
import com.ancientlore.stickies.BasicRecyclerAdapter
import com.ancientlore.stickies.C
import com.ancientlore.stickies.R
import com.ancientlore.stickies.SortField
import com.ancientlore.stickies.data.model.Note
import java.text.DateFormat
import java.util.*

class NotesListAdapter(context: Context, items: MutableList<Note>)
	: BasicRecyclerAdapter<Note, NotesListAdapter.ViewHolder>(context, items) {

	private var timeComparator = Comparator<Note> { o1, o2 -> o1.timeCreated.compareTo(o2.timeCreated) }
	private var titleComparator = Comparator<Note> { o1, o2 -> o1.title.compareTo(o2.title) }

	override fun getViewHolderLayoutRes(viewType: Int) = R.layout.notes_list_item

	override fun getViewHolder(layout: View) = ViewHolder(layout)

	override fun getDiffCallback(newItems: List<Note>) = DiffCallback(items, newItems)

	@UiThread
	override fun deleteItem(id: Long) = deleteItemAt(findPosition(id))

	override fun findItem(id: Long) = items.find { it.id == id }

	override fun findPosition(id: Long) = items.indexOfFirst { it.id == id }

	override fun isTheSame(first: Note, second: Note) = first.id == second.id

	override fun isUnique(item: Note) = items.none { it.id == item.id }

	override fun getSortComparator(@SortField sortField: String) = when (sortField) {
		C.FIELD_DATE -> timeComparator
		else -> titleComparator
	}

	class ViewHolder(itemView: View): BasicRecyclerAdapter.ViewHolder<Note>(itemView) {

		private val titleView = itemView.findViewById<TextView>(R.id.titleView)
		private val dateView = itemView.findViewById<TextView>(R.id.dateView)

		override fun bind(data: Note) {
			titleView.text = data.title
			dateView.text = data.getDateCreated(DateFormat.SHORT)
		}
	}

	class DiffCallback(private val oldItems: List<Note>,
					   private val newItems: List<Note>)
		: DiffUtil.Callback() {

		override fun getOldListSize() = oldItems.size

		override fun getNewListSize() = newItems.size

		override fun areItemsTheSame(oldPos: Int, newPos: Int) = oldItems[oldPos].id == newItems[newPos].id

		override fun areContentsTheSame(oldPos: Int, newPos: Int) = oldItems[oldPos] == newItems[newPos]
	}
}