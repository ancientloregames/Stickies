package com.ancientlore.stickies.menu.popup

import android.content.Context
import android.support.annotation.UiThread
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.ancientlore.stickies.databinding.PopupMenuBinding
import com.ancientlore.stickies.menu.MenuItem

class PopupMenu(context: Context) {

	private val window = PopupWindow(context)
	private val viewModel = PopupMenuViewModel(context)

	init {
		val binding = PopupMenuBinding.inflate(LayoutInflater.from(context), null)
		binding.viewModel = viewModel
		setupWindow(binding.root)
	}

	@UiThread
	fun setItems(items: List<MenuItem>) = viewModel.setAdapterItems(items)

	@UiThread
	fun show(anchor: View) {
		if (isNotShowing())
			window.showAsDropDown(anchor)
	}

	@UiThread
	fun hide() = window.dismiss()

	fun observeItemClicked() = viewModel.observeItemClicked()

	private fun setupWindow(view: View) {
		window.apply {
			width = ViewGroup.LayoutParams.WRAP_CONTENT
			height = ViewGroup.LayoutParams.WRAP_CONTENT
			contentView = view
			isFocusable = true
			isOutsideTouchable = true
		}
	}

	private fun isShowing() = window.isShowing

	private fun isNotShowing() = !isShowing()
}