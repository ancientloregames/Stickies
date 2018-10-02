package com.ancientlore.stickies.notedetail

import android.content.Intent
import com.ancientlore.stickies.R
import com.ancientlore.stickies.databinding.ActivityNotedetailBinding
import com.ancientlore.stickies.BasicActivity
import com.ancientlore.stickies.addeditnote.AddEditNoteActivity
import com.android.databinding.library.baseAdapters.BR

class NoteDetailActivity: BasicActivity<ActivityNotedetailBinding, NoteDetailViewModel>() {

	companion object {
		const val EXTRA_NOTE_ID = "extra_note_id"
	}

	override fun getLayoutId() = R.layout.activity_notedetail

	override fun getBindingVariable() = BR.viewModel

	override fun createViewModel() = NoteDetailViewModel(application, getNoteId())

	override fun getTitleId() = R.string.note_details

	override fun setupViewModel() {
		super.setupViewModel()

		viewModel.onEditNote()
				.takeUntil(destroyEvent)
				.subscribe { openNoteEditor(it) }
	}

	private fun getNoteId() = intent.getLongExtra(EXTRA_NOTE_ID, 0)
			.takeIf { it != 0L }
			?: throw RuntimeException("No id was passed to the NoteDetailActivity. It is mandatory!")

	private fun openNoteEditor(id: Long) {
		val intent = Intent(this, AddEditNoteActivity::class.java). apply {
			putExtra(AddEditNoteActivity.EXTRA_NOTE_ID, id)
		}
		startActivityForResult(intent, NoteDetailViewModel.INTENT_EDIT_NOTE)
	}
}