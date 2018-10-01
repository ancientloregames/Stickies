package com.ancientlore.stickies.notedetail

import com.ancientlore.stickies.R
import com.ancientlore.stickies.databinding.ActivityNotedetailBinding
import com.ancientlore.stickies.BasicActivity
import com.android.databinding.library.baseAdapters.BR

class NoteDetailActivity: BasicActivity<ActivityNotedetailBinding, NoteDetailViewModel>() {

	companion object {
		const val EXTRA_NOTE_ID = "extra_note_id"
	}

	override fun getLayoutId() = R.layout.activity_notedetail

	override fun getBindingVariable() = BR.viewModel

	override fun createViewModel() = NoteDetailViewModel(application, getNoteId())

	override fun getTitleId() = R.string.note_details

	private fun getNoteId() = intent.getLongExtra(EXTRA_NOTE_ID, 0)
			.takeIf { it != 0L }
			?: throw RuntimeException("No id was passed to the NoteDetailActivity. It is mandatory!")
}