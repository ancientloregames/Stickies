package com.ancientlore.stickies.popupmenu

import android.content.Context
import android.databinding.DataBindingUtil
import android.view.View
import android.view.ViewGroup
import com.ancientlore.stickies.BasicListAdapter
import com.ancientlore.stickies.databinding.PopupItemBinding

class MenuAdapter(context: Context)
	: BasicListAdapter<MenuItem>(context), MenuItemViewModel.Listener {

	override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
		val  binding = convertView?.let {
			DataBindingUtil.getBinding<PopupItemBinding>(convertView)
		} ?: PopupItemBinding.inflate(inflater, parent, false)

		val viewModel = MenuItemViewModel(getItem(position))
		binding.viewModel = viewModel

		viewModel.setListener(this)

		return binding.root
	}

	override fun onItemClicked(item: MenuItem) = itemClickedEvent.onNext(item)
}