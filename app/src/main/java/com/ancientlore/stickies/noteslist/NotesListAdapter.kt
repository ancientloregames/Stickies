package com.ancientlore.stickies.noteslist

import android.content.Context
import android.support.v7.util.DiffUtil
import android.view.View
import android.widget.TextView
import com.ancientlore.stickies.BasicListAdapter
import com.ancientlore.stickies.R
import com.ancientlore.stickies.data.model.Note

class NotesListAdapter(context: Context, items: MutableList<Note>)
	: BasicListAdapter<Note, NotesListAdapter.ViewHolder>(context, items) {

	private var timeComparator = Comparator<Note> { o1, o2 -> o1.timestamp.compareTo(o2.timestamp) }
	private var titleComparator = Comparator<Note> { o1, o2 -> o1.title.compareTo(o2.title) }

	init { setComparator(timeComparator) }

	override fun getViewHolderLayoutRes(viewType: Int) = R.layout.notes_list_item

	override fun getViewHolder(layout: View) = ViewHolder(layout)

	override fun isTheSame(first: Note, second: Note) = first.id == second.id

	override fun isUnique(item: Note) = items.none { it.id == item.id }

	override fun getDiffCallback(newItems: List<Note>) = DiffCallback(items, newItems)

	class ViewHolder(itemView: View): BasicListAdapter.ViewHolder<Note>(itemView) {

		private val titleView = itemView.findViewById<TextView>(R.id.titleView)

		override fun bind(data: Note) {
			titleView.text = data.title
		}
	}

	fun chooseComparator(sortField: String) {
		when (sortField) {
			"timestamp" -> setComparator(timeComparator)
			"title" -> setComparator(titleComparator)
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