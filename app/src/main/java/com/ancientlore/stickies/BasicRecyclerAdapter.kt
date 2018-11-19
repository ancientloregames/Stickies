package com.ancientlore.stickies

import android.content.Context
import android.databinding.ViewDataBinding
import android.support.annotation.CallSuper
import android.support.annotation.UiThread
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ancientlore.stickies.utils.recyclerdiff.HeadedRecyclerDiffUtil
import java.util.*

abstract class BasicRecyclerAdapter<P, T: BasicRecyclerAdapter.ViewHolder<P, B>, B: ViewDataBinding>(
		context: Context, items: MutableList<P>, withHeader: Boolean = false, withFooter: Boolean = false)
	: HeadedRecyclerAdapter<P, T>(items, withHeader, withFooter), MutableAdapter<P> {

	companion object {
		private const val VIEW_TYPE_ITEM = 0
	}

	protected val layoutInflater: LayoutInflater = LayoutInflater.from(context)

	abstract fun createItemViewDataBinding(parent: ViewGroup): B

	abstract fun getViewHolder(binding: B): T

	abstract fun getDiffCallback(newItems: List<P>): HeadedRecyclerDiffUtil.Callback

	abstract fun isTheSame(first: P, second: P) : Boolean

	abstract fun isUnique(item: P) : Boolean

	abstract fun getSortComparator(@SortField sortField: String): Comparator<P>

	override fun getItemViewTypeInner(position: Int) = VIEW_TYPE_ITEM

	override fun onCreateViewHolderInner(parent: ViewGroup, viewType: Int): T {
		val binding = createItemViewDataBinding(parent)
		return getViewHolder(binding)
	}

	@CallSuper
	override fun onBindViewHolderInner(holder: T, position: Int) = holder.bind(items[position])

	@UiThread
	override fun setItems(newItems: List<P>) {
		val diffResult = HeadedRecyclerDiffUtil.calculateDiff(getDiffCallback(newItems))

		items.clear()
		items.addAll(newItems)

		diffResult.dispatchUpdatesTo(this)
	}

	@UiThread
	override fun prependItem(newItem: P): Boolean {
		if (isUnique(newItem)) {
			items.add(0, newItem)
			notifyListItemInserted(0)
			return true
		}

		return false
	}

	@UiThread
	override fun addItem(newItem: P): Boolean {
		if (isUnique(newItem)) {
			items.add(newItem)
			notifyListItemInserted(itemCount - 1)
			return true
		}

		return false
	}

	@UiThread
	override fun updateItem(updatedItem: P) = updateItemAt(getItemPosition(updatedItem), updatedItem)

	@UiThread
	override fun deleteItem(itemToDelete: P) = deleteItemAt(getItemPosition(itemToDelete))

	override fun sort(@SortField field: String, @SortOrder order: String) {
		val comparator = getSortComparator(field)

		val orderedComparator = when (order) {
			C.ORDER_DESC -> Collections.reverseOrder(comparator)
			else -> comparator
		}

		val newList = items.toList()

		Collections.sort(newList, orderedComparator)

		setItems(newList)
	}

	override fun isEmpty() = items.isEmpty()

	private fun getViewHolderLayout(parent: ViewGroup, layoutRes: Int) = layoutInflater.inflate(layoutRes, parent, false)

	@UiThread
	protected fun deleteItemAt(position: Int): Boolean {
		if (isValidPosition(position)) {
			items.removeAt(position)
			notifyListItemRemoved(position)
			return true
		}

		return false
	}

	@UiThread
	private fun updateItemAt(position: Int, updatedItem: P): Boolean {
		if (isValidPosition(position)) {
			items[position] = updatedItem
			notifyListItemChanged(position)
			return true
		}

		return false
	}

	private fun getItemPosition(updatedItem: P) = items.indexOfFirst { isTheSame(it, updatedItem) }

	abstract class ViewHolder<T, B: ViewDataBinding>(protected val binding: B) : RecyclerView.ViewHolder(binding.root), Bindable<T>
}