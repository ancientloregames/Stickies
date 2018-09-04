package com.ancientlore.stickies

import android.content.Context
import android.view.View
import android.widget.TextView

class NotesListAdapter(context: Context, items: MutableList<Note>)
	: BasicListAdapter<Note, NotesListAdapter.ViewHolder>(context, items) {

	override fun getViewHolderLayoutRes(viewType: Int) = R.layout.notes_list_item

	override fun getViewHolder(layout: View) = ViewHolder(layout)

	override fun compareItems(first: Note, second: Note) = first.id > second.id

	class ViewHolder(itemView: View): BasicListAdapter.ViewHolder<Note>(itemView) {

		private val titleView = itemView.findViewById<TextView>(R.id.title)

		override fun bind(data: Note) {
			titleView.text = data.title
		}
	}
}