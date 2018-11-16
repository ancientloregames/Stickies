package com.ancientlore.stickies.menu

import android.os.Bundle
import android.support.annotation.AnyThread
import android.support.annotation.LayoutRes
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.ViewGroup

abstract class BasicBottomSheetDialog: BottomSheetDialogFragment() {

	protected abstract fun getFragmentTag(): String

	@LayoutRes
	protected abstract fun getLayoutResId(): Int

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = createView(inflater, container)

	private fun createView(inflater: LayoutInflater, container: ViewGroup?) = inflater.inflate(getLayoutResId(), container, false)!!

	fun show(manager: FragmentManager) {
		val transaction = manager.beginTransaction()
		manager.findFragmentByTag(getFragmentTag())
				?.let { transaction.remove(it) }
		transaction.addToBackStack(null)

		show(transaction, getFragmentTag())
	}

	@AnyThread
	fun hide() = activity?.runOnUiThread { dismiss() }
}