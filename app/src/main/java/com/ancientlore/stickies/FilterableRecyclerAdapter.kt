package com.ancientlore.stickies

import android.content.Context
import android.databinding.ViewDataBinding
import android.widget.Filter
import android.widget.Filterable

abstract class FilterableRecyclerAdapter<P, T: BasicRecyclerAdapter.ViewHolder<P, B>, B: ViewDataBinding>(
		context: Context,
		items: MutableList<P>,
		withHeader: Boolean = false,
		withFooter: Boolean = false)
	: BasicRecyclerAdapter<P, T, B>(context, items, withHeader, withFooter), Filterable {

	protected val fullList = mutableListOf<P>()

	private val filter: ListFilter by lazy { createFilter() }

	private var currentConstraint = ""

	abstract fun createFilter(): ListFilter

	override fun getFilter(): Filter = filter

	override fun setItems(newItems: List<P>) {
		fullList.clear()
		fullList.addAll(newItems)
		super.setItems(newItems)
	}

	override fun prependItem(newItem: P): Boolean {
		val wasAdded = super.prependItem(newItem)

		if (wasAdded) fullList.add(0, newItem)

		return wasAdded
	}

	override fun addItem(newItem: P): Boolean {
		val wasAdded = super.addItem(newItem)

		if (wasAdded) fullList.add(newItem)

		return wasAdded
	}

	override fun updateItem(updatedItem: P): Boolean {
		val position = getFullListPosition(updatedItem)

		if (position != -1) fullList[position] = updatedItem

		return super.updateItem(updatedItem)
	}

	override fun deleteItem(itemToDelete: P): Boolean {
		val position = getFullListPosition(itemToDelete)

		if (position != -1) items.removeAt(position)

		return super.deleteItem(itemToDelete)
	}

	fun filter(constraint: String) {
		currentConstraint = constraint
		filter.filter(constraint)
	}

	private fun getFullListPosition(updatedItem: P) = fullList.indexOfFirst { isTheSame(it, updatedItem) }

	private fun setFilteredItems(filteredItems: List<P>) {
		items.clear()
		items.addAll(filteredItems)
	}

	abstract inner class ListFilter: Filter() {

		abstract fun satisfy(item: P, candidate: String): Boolean

		override fun performFiltering(constraint: CharSequence?): FilterResults {
			val candidate = constraint?.toString()?.toLowerCase() ?: ""
			val resultList =
					if (candidate.isNotEmpty())
						fullList.filter { satisfy(it, candidate) }
					else fullList

			val result = FilterResults()
			result.count = resultList.size
			result.values = resultList

			return result
		}

		override fun publishResults(constraint: CharSequence?, results: FilterResults) {
			results.values?.let {
				setFilteredItems(it as MutableList<P>)
				notifyDataSetChanged()
			}
		}
	}
}