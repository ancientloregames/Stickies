package com.ancientlore.stickies.noteslist

import android.content.Context
import android.view.View
import android.widget.TextView
import com.ancientlore.stickies.BasicListAdapter
import com.ancientlore.stickies.R
import com.ancientlore.stickies.data.model.Note

class NotesListAdapter(context: Context, items: MutableList<Note>)
	: BasicListAdapter<Note, NotesListAdapter.ViewHolder>(context, items) {

	override fun getViewHolderLayoutRes(viewType: Int) = R.layout.notes_list_item

	override fun getViewHolder(layout: View) = ViewHolder(layout)

	override fun isTheSame(first: Note, second: Note) = first.id > second.id

	override fun isUnique(item: Note) = items.none { it.id == item.id }

	class ViewHolder(itemView: View): BasicListAdapter.ViewHolder<Note>(itemView) {

		private val titleView = itemView.findViewById<TextView>(R.id.titleView)

		override fun bind(data: Note) {
			titleView.text = data.title
		}
	}
}