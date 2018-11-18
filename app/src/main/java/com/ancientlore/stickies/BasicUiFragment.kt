package com.ancientlore.stickies

import android.content.Context
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BasicUiFragment<VM>: Fragment() {

	@LayoutRes
	protected abstract fun getLayoutResId(): Int

	protected abstract fun createViewModel(context: Context): VM

	protected abstract fun initViewModel(viewModel: VM)

	protected abstract fun bind(view: View, viewModel: VM)

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = createView(inflater, container)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val viewModel = createViewModel(view.context)

		initViewModel(viewModel)

		bind(view, viewModel)
	}

	private fun createView(inflater: LayoutInflater, container: ViewGroup?): View = inflater.inflate(getLayoutResId(), container, false)
}