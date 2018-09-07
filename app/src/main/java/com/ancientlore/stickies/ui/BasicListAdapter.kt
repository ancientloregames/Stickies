package com.ancientlore.stickies.ui

import android.content.Context
import android.support.annotation.CallSuper
import android.support.annotation.UiThread
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ancientlore.stickies.Bindable
import com.ancientlore.stickies.Clickable

abstract class BasicListAdapter<
		P,
		T: BasicListAdapter.ViewHolder<P>>
			(context: Context, internal val items: MutableList<P>)
	: RecyclerView.Adapter<T>() {

	interface Listener<P> {
		fun onItemSelected(item: P)
	}
	var listener: Listener<P>? = null

	private val layoutInflater = LayoutInflater.from(context)

	abstract fun getViewHolderLayoutRes(viewType: Int): Int

	abstract fun getViewHolder(layout: View): T

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
		holder.bind(items[index])
		holder.onClick(Runnable {
			listener?.onItemSelected(item)
		})
	}

	@UiThread
	fun addItem(newItem: P) {
		items.add(newItem)
		notifyItemInserted(itemCount - 1)
	}

	@UiThread
	fun updateItem(updatedItem: P) {
		items.indexOfFirst { compareItems(it, updatedItem) }.takeIf { it != -1 }
				?.let { index -> updateItemAt(index, updatedItem) }
	}

	@UiThread
	fun deleteItem(itemToDelete: P): Boolean {
		val index = items.indexOf(itemToDelete)
		if (index != -1) {
			items.removeAt(index)
			notifyItemRemoved(index)
			return true
		}
		return false
	}

	private fun updateItemAt(index: Int, updatedItem: P) {
		items[index] = updatedItem
		notifyItemChanged(index)
	}

	abstract fun compareItems(first: P, second: P) : Boolean

	abstract class ViewHolder<T>(itemView: View): RecyclerView.ViewHolder(itemView), Bindable<T>, Clickable {

		override fun onClick(action: Runnable) { itemView.setOnClickListener { action.run() } }
	}
}