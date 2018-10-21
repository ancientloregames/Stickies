package com.ancientlore.stickies.addeditnote

import android.os.Bundle
import com.ancientlore.stickies.R
import com.ancientlore.stickies.menu.MenuItem
import com.ancientlore.stickies.menu.bottomsheet.BottomMenuDialog

internal class AddEditMenuDialog: BottomMenuDialog() {

	companion object {
		fun newInstance(): AddEditMenuDialog {
			val dialog = AddEditMenuDialog()
			val args = Bundle()
			args.putParcelableArrayList(ARG_ITEMS, dialog.createMenu())
			dialog.arguments = args
			return dialog
		}
	}

	private fun createMenu(): ArrayList<MenuItem> {
		return arrayListOf<MenuItem>().apply {
			add(MenuItem(R.id.im_important, R.string.menu_important, R.drawable.ic_important_on))
			add(MenuItem(R.id.im_completed, R.string.menu_completed, R.drawable.ic_completed_on))
		}
	}
}