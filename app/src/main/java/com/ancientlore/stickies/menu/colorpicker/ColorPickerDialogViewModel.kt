package com.ancientlore.stickies.menu.colorpicker

import android.content.Context
import com.ancientlore.stickies.BasicListViewModel

class ColorPickerDialogViewModel(context: Context): BasicListViewModel<Int>(context) {
	override fun createListAdapter(context: Context) = ColorPickerAdapter(context)
}