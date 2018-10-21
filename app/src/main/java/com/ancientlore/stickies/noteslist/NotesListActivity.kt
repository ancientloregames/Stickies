package com.ancientlore.stickies.noteslist

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.PopupMenu
import com.ancientlore.stickies.*
import com.ancientlore.stickies.addeditnote.AddEditNoteActivity
import com.ancientlore.stickies.databinding.ActivityNoteslistBinding
import com.ancientlore.stickies.notedetail.NoteDetailActivity
import com.ancientlore.stickies.noteslist.NotesListViewModel.Companion.OPTION_FILTER
import com.ancientlore.stickies.noteslist.NotesListViewModel.Companion.OPTION_SORT
import com.ancientlore.stickies.sortdialog.SortDialogFragment
import kotlinx.android.synthetic.main.activity_noteslist.*

class NotesListActivity : BasicActivity<ActivityNoteslistBinding, NotesListViewModel>() {

	override fun getLayoutId() = R.layout.activity_noteslist

	override fun getBindingVariable() = BR.viewModel

	override fun createViewModel() = NotesListViewModel(application, getListAdapter())

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.notes_list_menu, menu)

		return true
	}

	override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
		R.id.filter -> viewModel.handleOptionSelection(OPTION_FILTER)
		R.id.sort -> viewModel.handleOptionSelection(OPTION_SORT)
		else -> super.onOptionsItemSelected(item)
	}

	override fun setupView(savedInstanceState: Bundle?) {
		super.setupView(savedInstanceState)
		setupActionBar()
		setupList()
	}

	override fun setupViewModel() {
		super.setupViewModel()

		subscriptions.add(viewModel.observeAddNote()
				.subscribe { startNoteAddition() })

		subscriptions.add(viewModel.observeEditNote()
				.subscribe { startNoteEditing(it) })

		subscriptions.add(viewModel.observeShowNote()
				.subscribe { openNoteDetails(it) })

		subscriptions.add(viewModel.observeShowFilterMenu()
				.subscribe { showFilterMenu() })

		subscriptions.add(viewModel.observeShowSortMenu()
				.subscribe { showSortMenu(it) })

		subscriptions.add(viewModel.observeScrollToTopRequest()
				.subscribe { notesListView.smoothScrollToPosition(0) })
	}

	private fun setupList() {
		val listAdapter = NotesListAdapter(this, mutableListOf())
		notesListView.adapter = listAdapter
	}

	private fun getListAdapter() = notesListView.adapter as NotesListAdapter

	private fun showFilterMenu() {
		val popup = PopupMenu(this, findViewById(R.id.filter))
		popup.menuInflater.inflate(R.menu.notes_list_filter_menu, popup.menu)

		popup.setOnMenuItemClickListener { onFilterSelected(it.itemId) }

		popup.show()
	}

	private fun onFilterSelected(itemId: Int): Boolean {
		when (itemId) {
			R.id.all -> viewModel.handleFilterSelection(NotesListViewModel.FILTER_ALL)
			R.id.important -> viewModel.handleFilterSelection(NotesListViewModel.FILTER_IMPORTANT)
			else -> return false
		}

		return true
	}

	private fun startNoteAddition() {
		val intent = Intent(this, AddEditNoteActivity::class.java)
		startActivityForResult(intent, NotesListViewModel.INTENT_ADD_NOTE)
	}

	private fun startNoteEditing(id: Long) {
		val intent = Intent(this, AddEditNoteActivity::class.java).apply {
			putExtra(C.EXTRA_NOTE_ID, id)
		}
		startActivityForResult(intent, NotesListViewModel.INTENT_EDIT_NOTE)
	}

	private fun openNoteDetails(id: Long) {
		val intent = Intent(this, NoteDetailActivity::class.java).apply {
			putExtra(C.EXTRA_NOTE_ID, id)
		}
		startActivityForResult(intent, NotesListViewModel.INTENT_SHOW_NOTE)
	}

	private fun showSortMenu(@SortOrder currentSortOrder: String) {
		val dialog = SortDialogFragment.newInstance(currentSortOrder)

		dialog.listener = object : SortDialogFragment.Listener {
			override fun onSortSelected(@SortField field: String, @SortOrder order: String) {
				viewModel.sort(field, order)
			}
		}

		dialog.show(supportFragmentManager, "SortDialog")
	}
}
