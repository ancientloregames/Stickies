package com.ancientlore.stickies

import android.content.Context
import android.widget.Filter
import android.widget.Filterable

abstract class FilterableListAdapter<T>(context: Context)
	: BasicListAdapter<T>(context), Filterable {

	internal val origItems = mutableListOf<T>()

	private val filter: ListFilter = createFilter()

	abstract fun createFilter(): ListFilter

	override fun getFilter() = filter

	private fun setFilteredItems(filteredItems: List<T>) {
		items.clear()
		items.addAll(filteredItems)
	}

	override fun setItems(items: List<T>) {
		origItems.clear()
		origItems.addAll(items)
		super.setItems(items)
	}

	abstract inner class ListFilter: Filter() {
		override fun performFiltering(constraint: CharSequence): FilterResults {
			val resultList =
					if (constraint.isNotEmpty()) {
						val candidate = constraint.toString().toLowerCase()

						origItems.filter { satisfy(it, candidate) }
					}
					else origItems

			val result = FilterResults()
			result.count = resultList.size
			result.values = resultList

			return result
		}

		abstract fun satisfy(item: T, candidate: String): Boolean

		override fun publishResults(constraint: CharSequence, results: FilterResults) {
			setFilteredItems(results.values as MutableList<T>)
			notifyDataSetChanged()
		}
	}
}