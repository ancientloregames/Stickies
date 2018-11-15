package com.ancientlore.stickies.menu.popup

import android.content.Context
import com.ancientlore.stickies.BasicListViewModel
import com.ancientlore.stickies.menu.MenuAdapter
import com.ancientlore.stickies.menu.MenuItem

class PopupMenuViewModel(context: Context): BasicListViewModel<MenuItem>(context) {
	override fun createListAdapter(context: Context) = MenuAdapter(context)
}