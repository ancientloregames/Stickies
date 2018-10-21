package com.ancientlore.stickies.menu.colorpicker

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.widget.ListAdapter

class ColorPickerDialogViewModel(context: Context): BaseObservable() {

	private val adapter = ColorPickerAdapter(context)

	fun setAdapterItems(items: List<Int>) = adapter.setItems(items)

	@Bindable
	fun getListAdapter() = adapter as ListAdapter

	fun observeItemClicked() = adapter.observeItemClicked()
}