package com.ancientlore.stickies.menu.colorpicker

import com.ancientlore.stickies.BasicListItemViewModel

class ColorPickerItemViewModel(color: Int): BasicListItemViewModel<Int>(color) {
	interface Listener: BasicListItemViewModel.Listener<Int>
}