package com.ancientlore.stickies.addeditnote

import android.app.Activity
import android.content.Intent
import android.view.Menu
import com.ancientlore.stickies.BR
import com.ancientlore.stickies.BasicActivity
import com.ancientlore.stickies.R
import com.ancientlore.stickies.databinding.ActivityAddeditnoteBinding

class AddEditNoteActivity : BasicActivity<ActivityAddeditnoteBinding, AddEditNoteViewModel>() {

	companion object {
		const val EXTRA_NOTE_ID = "extra_note_id"

		private const val DUMMY_ID = -1L
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.note_addedit_menu, menu)

		return true
	}

	override fun getLayoutId() = R.layout.activity_addeditnote

	override fun getBindingVariable() = BR.viewModel

	override fun createViewModel() = getNoteId().takeIf { isValidId(it) }
			?.let { AddEditNoteViewModel(application, it) }
			?: AddEditNoteViewModel(application)

	override fun getTitleId() = R.string.new_note

	override fun setupViewModel() {
		super.setupViewModel()

		subscriptions.add(viewModel.onNoteAdded()
				.subscribe { finishWithResult(it) })
	}

	private fun getNoteId() = intent.getLongExtra(EXTRA_NOTE_ID, DUMMY_ID)

	private fun isValidId(id: Long) = id != DUMMY_ID

	private fun finishWithResult(newNoteId: Long) {
		val intent = Intent().apply {
			putExtra(EXTRA_NOTE_ID, newNoteId)
		}
		setResult(Activity.RESULT_OK, intent)
		finish()
	}
}