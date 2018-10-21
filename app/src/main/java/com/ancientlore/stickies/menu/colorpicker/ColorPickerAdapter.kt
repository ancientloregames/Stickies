package com.ancientlore.stickies.menu.colorpicker

import android.content.Context
import android.databinding.DataBindingUtil
import android.view.View
import android.view.ViewGroup
import com.ancientlore.stickies.BasicListAdapter
import com.ancientlore.stickies.databinding.ColorPickerItemBinding

class ColorPickerAdapter(context: Context)
	: BasicListAdapter<Int>(context), ColorPickerItemViewModel.Listener {

	override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
		val  binding = convertView?.let { view ->
			DataBindingUtil.getBinding<ColorPickerItemBinding>(view)
		} ?: ColorPickerItemBinding.inflate(inflater, parent, false)

		val viewModel = ColorPickerItemViewModel(getItem(position))
		binding.viewModel = viewModel

		viewModel.setListener(this)

		return binding.root
	}

	override fun onItemClicked(item: Int) = itemClickedEvent.onNext(item)
}