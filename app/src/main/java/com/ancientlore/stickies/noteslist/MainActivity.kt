package com.ancientlore.stickies.noteslist

import android.content.Intent
import android.os.Bundle
import com.ancientlore.stickies.BR
import com.ancientlore.stickies.BasicActivity
import com.ancientlore.stickies.R
import com.ancientlore.stickies.databinding.ActivityMainBinding
import com.ancientlore.stickies.notedetail.NoteDetailActivity
import com.ancientlore.stickies.addeditnote.NoteActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BasicActivity<ActivityMainBinding, MainActivityViewModel>() {

	override fun getLayoutId() = R.layout.activity_main

	override fun getBindingVariable() = BR.viewModel

	override fun createViewModel() = MainActivityViewModel(application, getListAdapter())

	override fun getTitleId() = R.string.app_name

	override fun setupView(savedInstanceState: Bundle?) {
		super.setupView(savedInstanceState)

		setupList()
	}

	override fun setupViewModel() {
		super.setupViewModel()

		viewModel.onAddNote()
				.takeUntil(destroyEvent)
				.subscribe { startNoteAddition() }

		viewModel.onShowNote()
				.takeUntil(destroyEvent)
				.subscribe { openNoteDetails(it) }
	}

	private fun setupList() {
		val listAdapter = NotesListAdapter(this, mutableListOf())
		notesListView.adapter = listAdapter
	}

	private fun getListAdapter() = notesListView.adapter as NotesListAdapter

	private fun startNoteAddition() {
		val intent = Intent(this, NoteActivity::class.java)
		startActivityForResult(intent, MainActivityViewModel.INTENT_ADD_NOTE)
	}

	private fun openNoteDetails(id: Long) {
		val intent = Intent(this, NoteDetailActivity::class.java). apply {
			putExtra(NoteDetailActivity.EXTRA_NOTE_ID, id)
		}
		startActivityForResult(intent, MainActivityViewModel.INTENT_SHOW_NOTE)
	}
}
