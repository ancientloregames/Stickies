package com.ancientlore.stickies.menu.bottomsheet

import android.content.Context
import android.os.Bundle
import android.support.annotation.AnyThread
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ancientlore.stickies.R
import com.ancientlore.stickies.databinding.BottomMenuBinding
import com.ancientlore.stickies.menu.MenuItem
import io.reactivex.internal.disposables.ListCompositeDisposable

open class BottomMenuDialog: BottomSheetDialogFragment() {

	companion object {
		const val DEF_TAG = "optionsMenu"
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

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = createView(inflater, container)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val viewModel = createViewModel(view.context)

		bind(view, viewModel)
	}

	override fun onDestroyView() {
		subscriptions.dispose()

		super.onDestroyView()
	}

	fun show(manager: FragmentManager) {
		val transaction = manager.beginTransaction()
		manager.findFragmentByTag(DEF_TAG)
				?.let { transaction.remove(it) }
		transaction.addToBackStack(null)

		show(transaction, DEF_TAG)
	}

	@AnyThread
	fun hide() = activity?.runOnUiThread { dismiss() }

	fun setListener(listener: Listener) { this.listener = listener }

	private fun createView(inflater: LayoutInflater, container: ViewGroup?) = inflater.inflate(R.layout.bottom_menu, container, false)!!

	private fun createViewModel(context: Context): BottomMenuViewModel {
		val viewModel = BottomMenuViewModel(context)

		viewModel.setAdapterItems(getMenuItemsArg())

		subscriptions.add(viewModel.observeItemClicked().subscribe { listener?.onItemSelected(it) })

		return viewModel
	}

	private fun bind(view: View, viewModel: BottomMenuViewModel) {
		val binding = BottomMenuBinding.bind(view)
		binding.viewModel = viewModel
	}

	private fun getMenuItemsArg() = arguments?.getParcelableArrayList<MenuItem>(ARG_ITEMS)
			?: throw RuntimeException("Error! Menu items list must be passed as an argument!")
}