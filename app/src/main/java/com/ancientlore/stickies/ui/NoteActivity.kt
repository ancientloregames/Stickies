package com.ancientlore.stickies.ui

import com.ancientlore.stickies.BR
import com.ancientlore.stickies.R
import com.ancientlore.stickies.databinding.ActivityNoteBinding
import com.ancientlore.stickies.viewmodel.NoteActivityViewModel

class NoteActivity : BasicActivity<ActivityNoteBinding, NoteActivityViewModel>() {

	override fun getLayoutId() = R.layout.activity_note

	override fun getBindingVariable() = BR.viewModel

	override fun createViewModel() = NoteActivityViewModel(application)

	override fun getTitleId() = R.string.new_note
}