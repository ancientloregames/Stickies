package com.ancientlore.stickies.menu.bottomsheet

import android.content.Context
import android.os.Bundle
import android.view.View
import com.ancientlore.stickies.R
import com.ancientlore.stickies.databinding.BottomMenuBinding
import com.ancientlore.stickies.menu.MenuItem
import com.ancientlore.stickies.menu.MvvmBottomSheetDialog
import io.reactivex.internal.disposables.ListCompositeDisposable

open class BottomMenuDialog: MvvmBottomSheetDialog<BottomMenuViewModel>() {

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

	interface Listener {
		fun onItemSelected(item: MenuItem)
	}
	private var listener: Listener? = null

	private val subscriptions = ListCompositeDisposable()

	override fun getFragmentTag() = "bottomMenuDialog"

	override fun getLayoutResId() = R.layout.bottom_menu

	override fun onDestroyView() {
		subscriptions.dispose()

		super.onDestroyView()
	}

	override fun createViewModel(context: Context): BottomMenuViewModel {
		val viewModel = BottomMenuViewModel(context)

		viewModel.setAdapterItems(getMenuItemsArg())

		subscriptions.add(viewModel.observeItemClicked().subscribe { listener?.onItemSelected(it) })

		return viewModel
	}

	override fun bind(view: View, viewModel: BottomMenuViewModel) {
		val binding = BottomMenuBinding.bind(view)
		binding.viewModel = viewModel
	}

	fun setListener(listener: Listener) { this.listener = listener }

	private fun getMenuItemsArg() = arguments?.getParcelableArrayList<MenuItem>(ARG_ITEMS)
			?: throw RuntimeException("Error! Menu items list must be passed as an argument!")
}