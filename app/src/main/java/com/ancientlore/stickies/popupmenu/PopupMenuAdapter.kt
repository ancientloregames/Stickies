package com.ancientlore.stickies.popupmenu

import android.content.Context
import android.databinding.DataBindingUtil
import android.view.View
import android.view.ViewGroup
import com.ancientlore.stickies.BasicListAdapter
import com.ancientlore.stickies.databinding.PopupItemBinding

class PopupMenuAdapter(context: Context)
	: BasicListAdapter<PopupMenuItem>(context), PopupMenuItemViewModel.Listener {

	override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
		val  binding = convertView?.let {
			DataBindingUtil.getBinding<PopupItemBinding>(convertView)
		} ?: PopupItemBinding.inflate(inflater, parent, false)

		val viewModel = PopupMenuItemViewModel(getItem(position))
		binding.viewModel = viewModel

		viewModel.setListener(this)

		return binding.root
	}

	override fun onItemClicked(item: PopupMenuItem) = itemClickedEvent.onNext(item)
}