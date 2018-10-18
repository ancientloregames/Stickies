package com.ancientlore.stickies.popupmenu

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.widget.ListAdapter

class PopupMenuViewModel(context: Context): BaseObservable() {

	private val adapter = PopupMenuAdapter(context)

	fun setAdapterItems(items: List<PopupMenuItem>) = adapter.setItems(items)

	@Bindable
	fun getListAdapter() = adapter as ListAdapter

	fun observeItemClicked() = adapter.observeItemClicked()
}