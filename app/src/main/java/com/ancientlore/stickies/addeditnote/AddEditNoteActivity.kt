package com.ancientlore.stickies.addeditnote

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.Menu
import android.widget.Toast
import com.ancientlore.stickies.BR
import com.ancientlore.stickies.BasicActivity
import com.ancientlore.stickies.C
import com.ancientlore.stickies.R
import com.ancientlore.stickies.databinding.ActivityAddeditnoteBinding

class AddEditNoteActivity : BasicActivity<ActivityAddeditnoteBinding, AddEditNoteViewModel>() {

	companion object {
		const val TAG = "AddEditNoteActivity"
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.note_addedit_menu, menu)

		return true
	}

	override fun getLayoutId() = R.layout.activity_addeditnote

	override fun getBindingVariable() = BR.viewModel

	override fun createViewModel() = getNoteId().takeIf { isValidId(it) }
			?.let { AddEditNoteViewModel(application, it) }
			?: AddEditNoteViewModel(application)

	override fun setupActionBar() {
		super.setupActionBar()
		supportActionBar?.setDisplayShowTitleEnabled(false)
	}

	override fun setupViewModel() {
		super.setupViewModel()

		subscriptions.add(viewModel.observeNoteAdded()
				.subscribe { finishWithResult(it) })

		subscriptions.add(viewModel.observeAlert()
				.subscribe { showAlert(getAlertMessage(it)) })
	}

	private fun getNoteId() = intent.getLongExtra(C.EXTRA_NOTE_ID, C.DUMMY_ID)

	private fun isValidId(id: Long) = id != C.DUMMY_ID

	private fun finishWithResult(noteId: Long) {
		val intent = Intent().apply {
			putExtra(C.EXTRA_NOTE_ID, noteId)
		}
		setResult(Activity.RESULT_OK, intent)
		finish()
	}

	private fun showAlert(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

	private fun getAlertMessage(alertId: Int) = when (alertId) {
		AddEditNoteViewModel.ALERT_TITLE_EMPTY -> getString(R.string.alert_note_title_empty)
		AddEditNoteViewModel.ALERT_TITLE_LONG -> getString(R.string.alert_note_title_long)
		AddEditNoteViewModel.ALERT_BODY_LONG -> getString(R.string.alert_note_body_long)
		else -> {
			Log.w(TAG, "Unknown alert message id!")
			""
		}
	}
}