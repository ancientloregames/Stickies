package com.ancientlore.stickies.menu.popup

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.widget.ListAdapter
import com.ancientlore.stickies.menu.MenuAdapter
import com.ancientlore.stickies.menu.MenuItem

class PopupMenuViewModel(context: Context): BaseObservable() {

	private val adapter = MenuAdapter(context)

	fun setAdapterItems(items: List<MenuItem>) = adapter.setItems(items)

	@Bindable
	fun getListAdapter() = adapter as ListAdapter

	fun observeItemClicked() = adapter.observeItemClicked()
}