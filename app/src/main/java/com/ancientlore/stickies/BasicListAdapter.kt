package com.ancientlore.stickies

import android.content.Context
import android.support.annotation.CallSuper
import android.support.annotation.UiThread
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.*

abstract class BasicListAdapter<
		P,
		T: BasicListAdapter.ViewHolder<P>>
			(context: Context, internal val items: MutableList<P>)
	: RecyclerView.Adapter<T>(), MutableAdapter<P> {

	private val layoutInflater = LayoutInflater.from(context)

	private val itemSelectedEvent = PublishSubject.create<P>()

	abstract fun getViewHolderLayoutRes(viewType: Int): Int

	abstract fun getViewHolder(layout: View): T

	abstract fun getDiffCallback(newItems: List<P>): DiffUtil.Callback

	abstract fun isTheSame(first: P, second: P) : Boolean

	abstract fun isUnique(item: P) : Boolean

	abstract fun getSortComparator(@SortField sortField: String): Comparator<P>

	private fun getViewHolderLayout(parent: ViewGroup, layoutRes: Int) = layoutInflater.inflate(layoutRes, parent,false)

	override fun getItemCount() = items.count()

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): T {
		val layoutRes = getViewHolderLayoutRes(viewType)
		val layout = getViewHolderLayout(parent, layoutRes)
		return getViewHolder(layout)
	}

	@CallSuper
	override fun onBindViewHolder(holder: T, index: Int) {
		val item = items[index]
		holder.bind(item)
		holder.onClick(Runnable {
			itemSelectedEvent.onNext(item)
		})
	}

	@UiThread
	override fun setItems(newItems: List<P>) {
		val diffResult = DiffUtil.calculateDiff(getDiffCallback(newItems))

		items.clear()
		items.addAll(newItems)

		diffResult.dispatchUpdatesTo(this)
	}

	@UiThread
	override fun addItem(newItem: P): Boolean {
		if (isUnique(newItem)) {
			items.add(newItem)
			notifyItemInserted(itemCount - 1)
			return true
		}

		return false
	}

	@UiThread
	override fun updateItem(updatedItem: P): Boolean {
		val position = getItemPosition(updatedItem)
		if (position != -1) {
			updateItemAt(position, updatedItem)
			return true
		}

		return false
	}

	@UiThread
	override fun deleteItem(itemToDelete: P): Boolean {
		val position = getItemPosition(itemToDelete)
		if (position != -1) {
			items.removeAt(position)
			notifyItemRemoved(position)
			return true
		}

		return false
	}

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

	override fun onItemSelected() = itemSelectedEvent as Observable<P>

	@UiThread
	protected fun deleteItemAt(position: Int): Boolean {
		if (isValidPosition(position)) {
			items.removeAt(position)
			notifyItemRemoved(position)
			return true
		}

		return false
	}

	@UiThread
	private fun updateItemAt(index: Int, updatedItem: P) {
		items[index] = updatedItem
		notifyItemChanged(index)
	}

	private fun isValidPosition(position: Int) = position > -1 && position < items.size

	private fun getItemPosition(updatedItem: P) = items.indexOfFirst { isTheSame(it, updatedItem) }

	abstract class ViewHolder<T>(itemView: View): RecyclerView.ViewHolder(itemView), Bindable<T>, Clickable {

		override fun onClick(action: Runnable) { itemView.setOnClickListener { action.run() } }
	}
}