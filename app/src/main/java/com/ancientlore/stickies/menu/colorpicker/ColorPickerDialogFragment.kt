package com.ancientlore.stickies.menu.colorpicker

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.annotation.AnyThread
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ancientlore.stickies.R
import com.ancientlore.stickies.databinding.ColorPickerDialogBinding
import io.reactivex.internal.disposables.ListCompositeDisposable

class ColorPickerDialogFragment: DialogFragment() {
	companion object {
		const val DEF_TAG = "colorPicker"

		const val ARG_COLORS = "arg_colors"
		const val ARG_SELECTED = "arg_selected"

		fun newInstance(colors: IntArray, selected: Int): ColorPickerDialogFragment {
			val dialog = ColorPickerDialogFragment()
			val args = Bundle()
			args.putIntArray(ARG_COLORS, colors)
			args.putInt(ARG_SELECTED, selected)
			dialog.arguments = args
			return dialog
		}
	}

	interface Listener {
		fun onColorPicked(color: Int)
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

	fun setListener(listener: Listener)  { this.listener = listener }

	private fun createView(inflater: LayoutInflater, container: ViewGroup?): View {
		val view = inflater.inflate(R.layout.color_picker_dialog, container)!!

		setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme)
		dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

		return view
	}

	private fun createViewModel(context: Context): ColorPickerDialogViewModel {
		val viewModel = ColorPickerDialogViewModel(context)

		viewModel.setAdapterItems(getColorsArg())

		subscriptions.add(viewModel.observeItemClicked()
				.subscribe { listener?.onColorPicked(it) })

		return viewModel
	}

	private fun bind(view: View, viewModel: ColorPickerDialogViewModel) {
		val binding = ColorPickerDialogBinding.bind(view)
		binding.viewModel = viewModel
	}

	private fun getColorsArg() = arguments?.getIntArray(ARG_COLORS)?.toList()
			?: throw RuntimeException("Error! Color list must be passed as an argument!")
}