package com.ancientlore.stickies

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.widget.ListAdapter

abstract class BasicListViewModel<T>(context: Context): BaseObservable() {

	private val adapter = createListAdapter(context)

	protected abstract fun createListAdapter(context: Context): BasicListAdapter<T>

	fun setAdapterItems(items: List<T>) = adapter.setItems(items)

	@Bindable
	fun getListAdapter() = adapter as ListAdapter

	fun observeItemClicked() = adapter.observeItemClicked()
}