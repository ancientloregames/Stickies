package com.ancientlore.stickies.sortdialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ancientlore.stickies.*
import com.ancientlore.stickies.databinding.DialogSortBinding
import io.reactivex.internal.disposables.ListCompositeDisposable

class SortDialogFragment: DialogFragment() {

	companion object {
		const val ARG_SORT_ORDER = "arg_sortorder"

		fun newInstance(@SortOrder currentOrder: String): SortDialogFragment {
			val dialog = SortDialogFragment()
			val args = Bundle()
			args.putString(ARG_SORT_ORDER, currentOrder)
			dialog.arguments = args
			return dialog
		}
	}

	interface Listener {
		fun onSortSelected(@SortField field: String, @SortOrder order: String)
	}
	var listener: Listener? = null

	private lateinit var viewModel: SortDialogViewModel

	private val subscriptions = ListCompositeDisposable()

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.dialog_sort, container)

		setupView()

		setupViewModel(view)

		subscribeOnViewModel()

		return view
	}

	override fun show(manager: FragmentManager, tag: String?) {
		val transaction = manager.beginTransaction()
		manager.findFragmentByTag(tag)
				?.let { transaction.remove(it) }
		transaction.addToBackStack(null)

		show(transaction, tag)
	}

	private fun setupView() {
		setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme)
		dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
	}

	private fun setupViewModel(view: View) {
		val binder = DialogSortBinding.bind(view)
		viewModel = SortDialogViewModel(activity!!.application, getSortOrderArg())
		binder.viewModel = viewModel
	}

	override fun onDestroyView() {
		subscriptions.dispose()

		super.onDestroyView()
	}

	private fun getSortOrderArg() = arguments?.getString(ARG_SORT_ORDER)
			?: throw RuntimeException("Error! Current sorting order must be passed as an argument!")

	private fun subscribeOnViewModel() {
		subscriptions.add(viewModel.observeByTitleClicked()
				.subscribe { order -> onSortSelected(C.FIELD_TITLE, order) })

		subscriptions.add(viewModel.observeByDateCreationClicked()
				.subscribe { order -> onSortSelected(C.FIELD_DATE, order) })
	}

	private fun onSortSelected(@SortField field: String, @SortOrder order: String) {
		listener?.onSortSelected(field, order)
		dismiss()
	}
}