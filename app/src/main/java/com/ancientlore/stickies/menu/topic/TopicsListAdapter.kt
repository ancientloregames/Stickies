package com.ancientlore.stickies.menu.topic

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ancientlore.stickies.FilterableListAdapter
import com.ancientlore.stickies.data.model.Topic

class TopicsListAdapter(context: Context): FilterableListAdapter<Topic>(context) {

	override fun createFilter() = TopicFilter()

	override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
		val view = (convertView ?: inflater.inflate(android.R.layout.simple_list_item_1, parent, false)) as TextView

		val item = getItem(position)

		view.text = item.name

		return view
	}

	inner class TopicFilter: ListFilter() {
		override fun satisfy(item: Topic, candidate: String) = item.name.toLowerCase().startsWith(candidate)
	}
}