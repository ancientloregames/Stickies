package com.ancientlore.stickies.noteslist

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.PopupMenu
import com.ancientlore.stickies.BR
import com.ancientlore.stickies.BasicActivity
import com.ancientlore.stickies.R
import com.ancientlore.stickies.addeditnote.AddEditNoteActivity
import com.ancientlore.stickies.databinding.ActivityNoteslistBinding
import com.ancientlore.stickies.notedetail.NoteDetailActivity
import kotlinx.android.synthetic.main.activity_noteslist.*

class NotesListActivity : BasicActivity<ActivityNoteslistBinding, NotesListViewModel>() {

	override fun getLayoutId() = R.layout.activity_noteslist

	override fun getBindingVariable() = BR.viewModel

	override fun createViewModel() = NotesListViewModel(application, getListAdapter())

	override fun getTitleId() = R.string.app_name

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.notes_list_menu, menu)

		return true
	}

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

		viewModel.onShowFilterMenu()
				.takeUntil(destroyEvent)
				.subscribe { showFilterMenu() }
	}

	private fun setupList() {
		val listAdapter = NotesListAdapter(this, mutableListOf())
		notesListView.adapter = listAdapter
	}

	private fun getListAdapter() = notesListView.adapter as NotesListAdapter

	private fun showFilterMenu() {
		val popup = PopupMenu(this, findViewById(R.id.filter))
		popup.menuInflater.inflate(R.menu.notes_list_filter_menu, popup.menu)

		popup.setOnMenuItemClickListener { viewModel.handleFilterSelected(it.itemId) }

		popup.show()
	}

	private fun startNoteAddition() {
		val intent = Intent(this, AddEditNoteActivity::class.java)
		startActivityForResult(intent, NotesListViewModel.INTENT_ADD_NOTE)
	}

	private fun openNoteDetails(id: Long) {
		val intent = Intent(this, NoteDetailActivity::class.java). apply {
			putExtra(NoteDetailActivity.EXTRA_NOTE_ID, id)
		}
		startActivityForResult(intent, NotesListViewModel.INTENT_SHOW_NOTE)
	}
}
