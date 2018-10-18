package com.ancientlore.stickies.popupmenu

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.widget.ListAdapter

class PopupMenuViewModel(context: Context): BaseObservable() {

	private val adapter = MenuAdapter(context)

	fun setAdapterItems(items: List<MenuItem>) = adapter.setItems(items)

	@Bindable
	fun getListAdapter() = adapter as ListAdapter

	fun observeItemClicked() = adapter.observeItemClicked()
}