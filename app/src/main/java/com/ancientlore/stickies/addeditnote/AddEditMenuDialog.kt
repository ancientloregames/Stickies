package com.ancientlore.stickies.addeditnote

import android.os.Bundle
import com.ancientlore.stickies.R
import com.ancientlore.stickies.menu.MenuItem
import com.ancientlore.stickies.menu.bottomsheet.BottomMenuDialog

internal class AddEditMenuDialog: BottomMenuDialog() {

	companion object {
		fun newInstance(currentState: AddEditNoteViewModel.State): AddEditMenuDialog {
			val dialog = AddEditMenuDialog()
			val args = Bundle()
			args.putParcelableArrayList(ARG_ITEMS, dialog.createMenu(currentState))
			dialog.arguments = args
			return dialog
		}
	}

	private fun createMenu(currentState: AddEditNoteViewModel.State): ArrayList<MenuItem> {
		return arrayListOf<MenuItem>().apply {
			add(getImportanceMenuItem(currentState.isImportant))
			add(getCompletedMenuItem(currentState.isCompleted))
			add(MenuItem(R.id.im_colorpicker, R.string.menu_colorpicker, R.drawable.ic_colorpicker))
			add(MenuItem(R.id.im_alarm, R.string.menu_schedule_reminder, R.drawable.ic_alarm))
		}
	}

	private fun getImportanceMenuItem(isImportant: Boolean): MenuItem {
		return when (isImportant) {
			true -> MenuItem(R.id.im_important, R.string.menu_unimportant_full, R.drawable.ic_important_off)
			else -> MenuItem(R.id.im_important, R.string.menu_important_full, R.drawable.ic_important_on)
		}
	}

	private fun getCompletedMenuItem(isCompleted: Boolean): MenuItem {
		return when (isCompleted) {
			true -> MenuItem(R.id.im_completed, R.string.menu_uncompleted_full, R.drawable.ic_completed_off)
			else -> MenuItem(R.id.im_completed, R.string.menu_completed_full, R.drawable.ic_completed_on)
		}
	}
}