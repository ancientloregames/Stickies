package com.ancientlore.stickies.popupmenu

import android.databinding.BaseObservable

class PopupMenuItemViewModel(val item: PopupMenuItem): BaseObservable() {

	interface Listener {
		fun onItemClicked(item: PopupMenuItem)
	}
	private var listener: Listener? = null

	fun onClicked() = listener?.onItemClicked(item)

	fun setListener(listener: Listener) { this.listener = listener }
}