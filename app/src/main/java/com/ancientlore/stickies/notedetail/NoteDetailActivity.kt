package com.ancientlore.stickies.notedetail

import com.ancientlore.stickies.R
import com.ancientlore.stickies.databinding.ActivityNotedetailBinding
import com.ancientlore.stickies.ui.BasicActivity
import com.android.databinding.library.baseAdapters.BR

class NoteDetailActivity: BasicActivity<ActivityNotedetailBinding, NoteDetailViewModel>() {

	override fun getLayoutId() = R.layout.activity_notedetail

	override fun getBindingVariable() = BR.viewModel

	override fun createViewModel() = NoteDetailViewModel(application)

	override fun getTitleId() = R.string.note_details
}