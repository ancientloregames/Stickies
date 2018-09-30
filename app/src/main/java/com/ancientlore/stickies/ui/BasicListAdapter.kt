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
import com.ancientlore.stickies.MutableAdapter

abstract class BasicListAdapter<
		P,
		T: BasicListAdapter.ViewHolder<P>>
			(context: Context, internal val items: MutableList<P>)
	: RecyclerView.Adapter<T>(), MutableAdapter<P> {

	interface Listener<P> {
		fun onItemSelected(item: P)
	}
	var listener: Listener<P>? = null

	private val layoutInflater = LayoutInflater.from(context)

	abstract fun getViewHolderLayoutRes(viewType: Int): Int

	abstract fun getViewHolder(layout: View): T

	abstract fun isTheSame(first: P, second: P) : Boolean

	abstract fun isUnique(item: P) : Boolean

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
			listener?.onItemSelected(item)
		})
	}

	@UiThread
	override fun setItems(newItems: List<P>) {
		items.clear()
		items.addAll(newItems)
		notifyDataSetChanged()
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

	@UiThread
	private fun updateItemAt(index: Int, updatedItem: P) {
		items[index] = updatedItem
		notifyItemChanged(index)
	}

	private fun getItemPosition(updatedItem: P) = items.indexOfFirst { isTheSame(it, updatedItem) }

	abstract class ViewHolder<T>(itemView: View): RecyclerView.ViewHolder(itemView), Bindable<T>, Clickable {

		override fun onClick(action: Runnable) { itemView.setOnClickListener { action.run() } }
	}
}