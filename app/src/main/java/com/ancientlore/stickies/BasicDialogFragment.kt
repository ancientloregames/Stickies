package com.ancientlore.stickies

import android.content.Context
import android.databinding.BaseObservable
import android.os.Bundle
import android.support.annotation.AnyThread
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BasicDialogFragment<T: BaseObservable>: DialogFragment() {

	protected abstract fun getFragmentTag(): String

	protected abstract fun createView(inflater: LayoutInflater, container: ViewGroup?): View

	protected abstract fun createViewModel(context: Context): T

	protected abstract fun bind(view: View, viewModel: T)

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return createView(inflater, container)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val viewModel = createViewModel(view.context)

		bind(view, viewModel)
	}

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