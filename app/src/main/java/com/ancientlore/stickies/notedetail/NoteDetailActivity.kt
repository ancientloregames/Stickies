package com.ancientlore.stickies.notedetail

import android.app.Activity
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import com.ancientlore.stickies.BasicActivity
import com.ancientlore.stickies.R
import com.ancientlore.stickies.addeditnote.AddEditNoteActivity
import com.ancientlore.stickies.databinding.ActivityNotedetailBinding
import com.android.databinding.library.baseAdapters.BR

class NoteDetailActivity: BasicActivity<ActivityNotedetailBinding, NoteDetailViewModel>() {

	companion object {
		const val EXTRA_NOTE_ID = "extra_note_id"
	}

	override fun getLayoutId() = R.layout.activity_notedetail

	override fun getBindingVariable() = BR.viewModel

	override fun createViewModel() = NoteDetailViewModel(application, getNoteId())

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.note_detail_menu, menu)

		return true
	}

	override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
		R.id.delete -> viewModel.handleOptionSelection(NoteDetailViewModel.OPTION_DELETE)
		else -> super.onOptionsItemSelected(item)
	}

	override fun setupActionBar() {
		super.setupActionBar()
		supportActionBar?.setDisplayShowTitleEnabled(false)
	}

	override fun setupViewModel() {
		super.setupViewModel()

		subscriptions.add(viewModel.observeNoteEditing()
				.subscribe { openNoteEditor(it) })

		subscriptions.add(viewModel.observeNoteDeletion()
				.subscribe { finishWithDeletedId(it) })
	}

	private fun getNoteId() = intent.getLongExtra(EXTRA_NOTE_ID, 0)
			.takeIf { it != 0L }
			?: throw RuntimeException("No id was passed to the NoteDetailActivity. It is mandatory!")

	private fun openNoteEditor(id: Long) {
		val intent = Intent(this, AddEditNoteActivity::class.java). apply {
			putExtra(AddEditNoteActivity.EXTRA_NOTE_ID, id)
		}
		startActivity(intent)
		finish()
	}

	private fun finishWithDeletedId(id: Long) {
		val intent = Intent().apply {
			putExtra(EXTRA_NOTE_ID, id)
		}
		setResult(Activity.RESULT_OK, intent)
		finish()
	}
}