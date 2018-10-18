package com.ancientlore.stickies

import android.content.Context
import android.support.annotation.UiThread
import android.view.LayoutInflater
import android.widget.BaseAdapter
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

abstract class BasicListAdapter<T>(context: Context)
	: BaseAdapter() {

	protected val inflater = LayoutInflater.from(context)

	private val items = mutableListOf<T>()

	protected val itemClickedEvent = PublishSubject.create<T>()

	override fun getCount() = items.size

	override fun getItem(position: Int) = items[position]

	override fun getItemId(position: Int) = position.toLong()

	@UiThread
	fun setItems(items: List<T>) {
		this.items.clear()
		this.items.addAll(items)
		notifyDataSetChanged()
	}

	fun observeItemClicked() = itemClickedEvent as Observable<T>
}