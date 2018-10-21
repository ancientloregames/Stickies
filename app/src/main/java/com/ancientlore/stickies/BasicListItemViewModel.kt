package com.ancientlore.stickies

import android.databinding.BaseObservable

abstract class BasicListItemViewModel<T>(val item: T): BaseObservable() {

	interface Listener<T> {
		fun onItemClicked(item: T)
	}
	private var listener: Listener<T>? = null

	fun onClicked() = listener?.onItemClicked(item)

	fun setListener(listener: Listener<T>) { this.listener = listener }
}