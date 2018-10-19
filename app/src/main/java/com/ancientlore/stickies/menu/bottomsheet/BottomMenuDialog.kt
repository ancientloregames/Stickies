package com.ancientlore.stickies.menu.bottomsheet

import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ancientlore.stickies.R
import com.ancientlore.stickies.databinding.BottomMenuBinding
import com.ancientlore.stickies.menu.MenuItem

class BottomMenuDialog: BottomSheetDialogFragment() {

	companion object {
		const val ARG_ITEMS = "arg_items"

		fun newInstance(items: ArrayList<MenuItem>): BottomMenuDialog {
			val dialog = BottomMenuDialog()
			val args = Bundle()
			args.putParcelableArrayList(ARG_ITEMS, items)
			dialog.arguments = args
			return dialog
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = createView(inflater, container)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val viewModel = createViewModel(view.context)

		setupViewModel(viewModel)

		bind(view, viewModel)
	}

	override fun show(manager: FragmentManager, tag: String?) {
		val transaction = manager.beginTransaction()
		manager.findFragmentByTag(tag)
				?.let { transaction.remove(it) }
		transaction.addToBackStack(null)

		show(transaction, tag)
	}

	fun hide() = activity?.runOnUiThread { dismiss() }

	private fun createView(inflater: LayoutInflater, container: ViewGroup?) = inflater.inflate(R.layout.bottom_menu, container, false)!!

	private fun createViewModel(context: Context) = BottomMenuViewModel(context)

	private fun createViewDataBinding(view: View) = BottomMenuBinding.bind(view)

	private fun setupViewModel(viewModel: BottomMenuViewModel) = viewModel.setAdapterItems(getMenuItemsArg())

	private fun bind(view: View, viewModel: BottomMenuViewModel) {
		val binding = createViewDataBinding(view)
		binding.viewModel = viewModel
	}

	private fun getMenuItemsArg() = arguments?.getParcelableArrayList<MenuItem>(ARG_ITEMS)
			?: throw RuntimeException("Error! Menu items list must be passed as an argument!")
}