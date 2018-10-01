package com.ancientlore.stickies.addeditnote

import android.app.Activity
import android.content.Intent
import com.ancientlore.stickies.BR
import com.ancientlore.stickies.BasicActivity
import com.ancientlore.stickies.R
import com.ancientlore.stickies.databinding.ActivityNoteBinding

class NoteActivity : BasicActivity<ActivityNoteBinding, NoteActivityViewModel>() {

	companion object {
		const val EXTRA_NOTE_ID = "extra_note_id"
	}

	override fun getLayoutId() = R.layout.activity_note

	override fun getBindingVariable() = BR.viewModel

	override fun createViewModel() = NoteActivityViewModel(application)

	override fun getTitleId() = R.string.new_note

	override fun setupViewModel() {
		super.setupViewModel()

		viewModel.onNoteAdded()
				.takeUntil(destroyEvent)
				.subscribe { finishWithResult(it) }
	}

	private fun finishWithResult(newNoteId: Long) {
		val intent = Intent().apply {
			putExtra(EXTRA_NOTE_ID, newNoteId)
		}
		setResult(Activity.RESULT_OK, intent)
		finish()
	}
}