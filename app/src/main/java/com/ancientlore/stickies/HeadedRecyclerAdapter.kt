package com.ancientlore.stickies

import android.support.annotation.UiThread
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

abstract class HeadedRecyclerAdapter<P, T: RecyclerView.ViewHolder>(
		protected val items: MutableList<P>,
		private val withHeader: Boolean = false,
		private val withFooter: Boolean = false)
	: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	companion object {
		private const val VIEW_TYPE_HEADER = Int.MIN_VALUE
		private const val VIEW_TYPE_FOOTER = Int.MAX_VALUE
	}

	protected abstract fun getItemViewTypeInner(position: Int): Int

	protected abstract fun onCreateViewHolderInner(parent: ViewGroup, viewType: Int): T

	protected abstract fun onBindViewHolderInner(holder: T, position: Int)

	final override fun getItemCount(): Int {
		var itemCount = items.size
		if (withHeader) itemCount++
		if (withFooter) itemCount++
		return itemCount
	}

	final override fun getItemViewType(position: Int): Int {
		if (withHeader && isFirstPosition(position))
			return VIEW_TYPE_HEADER
		if (withFooter && isLastPosition(position))
			return VIEW_TYPE_FOOTER
		return getItemViewTypeInner(getItemPosition(position))
	}

	final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		if (withHeader && viewType == VIEW_TYPE_HEADER)
			return createHeaderViewHolder(parent)
		if (withFooter && viewType == VIEW_TYPE_FOOTER)
			return createFooterViewHolder(parent)
		return onCreateViewHolderInner(parent, viewType)
	}

	final override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		if (withHeader && isFirstPosition(position))
			bindHeaderViewHolder(holder)
		else if (withFooter && isLastPosition(position))
			bindFooterViewHolder(holder)
		else onBindViewHolderInner(holder as T, getItemPosition(position))
	}

	@UiThread
	fun notifyHeaderChanged() = notifyItemChanged(0)

	@UiThread
	fun notifyFooterChanged() = notifyItemChanged(getLastPosition())

	@UiThread
	fun notifyListItemChanged(position: Int) = notifyItemChanged(getViewPosition(position))

	@UiThread
	fun notifyListItemInserted(position: Int) = notifyItemInserted(getViewPosition(position))

	@UiThread
	fun notifyListItemRemoved(position: Int) = notifyItemRemoved(getViewPosition(position))

	@UiThread
	fun notifyListItemMoved(fromPos: Int, toPos: Int) = notifyItemMoved(getViewPosition(fromPos), getViewPosition(toPos))

	@UiThread
	fun notifyListItemsChanged(startPos: Int, count: Int) = notifyItemRangeChanged(getViewPosition(startPos), count)

	@UiThread
	fun notifyListItemRangeInserted(startPos: Int, count: Int) = notifyItemRangeInserted(getViewPosition(startPos), count)

	@UiThread
	fun notifyListItemRangeRemoved(startPos: Int, count: Int) = notifyItemRangeRemoved(getViewPosition(startPos), count)

	@UiThread
	fun notifyListItemRangeChanged(startPos: Int, count: Int, payload: Any?) = notifyItemRangeChanged(getViewPosition(startPos), count, payload)

	protected open fun createHeaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
		throw RuntimeException("If you use header in the list, you ought to override this method and provide a proper ViewHolder")
	}

	protected open fun createFooterViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
		throw RuntimeException("If you use footer in the list, you ought to override this method and provide a proper ViewHolder")
	}

	protected open fun bindHeaderViewHolder(holder: RecyclerView.ViewHolder) {}

	protected open fun bindFooterViewHolder(holder: RecyclerView.ViewHolder) {}

	protected fun isValidPosition(position: Int) = position > -1 && position < items.size

	private fun isFirstPosition(position: Int) = position == 0

	private fun isLastPosition(position: Int) = position == getLastPosition()

	private fun getLastPosition() = itemCount - 1

	private fun getItemPosition(viewPosition: Int) = if (withHeader) viewPosition - 1 else viewPosition

	private fun getViewPosition(itemPosition: Int) = if (withHeader) itemPosition + 1 else itemPosition
}