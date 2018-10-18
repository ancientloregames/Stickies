package com.ancientlore.stickies.menu

import android.databinding.BaseObservable

class MenuItemViewModel(val item: MenuItem): BaseObservable() {

	interface Listener {
		fun onItemClicked(item: MenuItem)
	}
	private var listener: Listener? = null

	fun onClicked() = listener?.onItemClicked(item)

	fun setListener(listener: Listener) { this.listener = listener }
}