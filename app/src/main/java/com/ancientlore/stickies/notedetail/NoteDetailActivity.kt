package com.ancientlore.stickies.notedetail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.ancientlore.stickies.BasicActivity
import com.ancientlore.stickies.C
import com.ancientlore.stickies.R
import com.ancientlore.stickies.databinding.ActivityNotedetailBinding
import com.android.databinding.library.baseAdapters.BR

class NoteDetailActivity: BasicActivity<ActivityNotedetailBinding, NoteDetailViewModel>() {

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

	override fun setupView(savedInstanceState: Bundle?) {
		super.setupView(savedInstanceState)
		setupActionBar()
	}

	override fun setupActionBar() {
		super.setupActionBar()
		supportActionBar?.setDisplayShowTitleEnabled(false)
	}

	override fun setupViewModel() {
		super.setupViewModel()

		subscriptions.add(viewModel.observeNoteEditing()
				.subscribe { finishWithResult(C.ACTION_EDIT, it) })

		subscriptions.add(viewModel.observeNoteDeletion()
				.subscribe { finishWithDeletionRequest(it) })
	}

	private fun getNoteId() = intent.getLongExtra(C.EXTRA_NOTE_ID, C.DUMMY_ID)
			.takeIf { it != C.DUMMY_ID }
			?: throw RuntimeException("No id was passed to the NoteDetailActivity. It is mandatory!")

	private fun finishWithResult(action: String, noteId: Long) {
		val intent = Intent(). apply {
			putExtra(C.EXTRA_NOTE_ID, noteId)
			this.action = action
		}
		setResult(Activity.RESULT_OK, intent)
		finish()
	}

	private fun finishWithDeletionRequest(id: Long) {
		val intent = Intent().apply {
			putExtra(C.EXTRA_NOTE_ID, id)
			action = C.ACTION_DELETE
		}
		setResult(Activity.RESULT_OK, intent)
		finish()
	}
}