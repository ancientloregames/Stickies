package com.ancientlore.stickies.menu

import com.ancientlore.stickies.BasicListItemViewModel

class MenuItemViewModel(item: MenuItem): BasicListItemViewModel<MenuItem>(item) {
	interface Listener: BasicListItemViewModel.Listener<MenuItem>
}