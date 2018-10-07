package com.ancientlore.stickies.addeditnote

import android.app.Application
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.util.Log
import com.ancientlore.stickies.BasicViewModel
import com.ancientlore.stickies.R
import com.ancientlore.stickies.data.model.Note
import com.ancientlore.stickies.data.source.DataSource
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class AddEditNoteViewModel(application: Application): BasicViewModel(application) {

	companion object {
		private const val TAG = "NoteActivityViewModel"
	}

	val titleField = ObservableField<String>("")
	val messageField = ObservableField<String>("")
	val isImportant = ObservableBoolean(false)

	private var noteId: Long = 0

	private val isValid get() = titleField.get()?.isNotEmpty() ?: false

	private val note get() = Note(noteId, System.currentTimeMillis(), titleField.get()!!, messageField.get()!!, isImportant.get())

	private val onNoteAdded = PublishSubject.create<Long>()

	constructor(application: Application, noteId: Long) : this(application) {
		loadNote(noteId)
	}

	override fun handleOptionSelection(optionId: Int): Boolean {
		when (optionId) {
			R.id.important -> switchImportance()
		}

		return true
	}

	private fun loadNote(id: Long) {
		repository.getItem(id, object : DataSource.ItemLoadedCallback<Note> {
			override fun onSuccess(data: Note) = bind(data)
			override fun onFailure(error: Throwable) {
				Log.w(TAG, error.message ?: "Some error occurred during the loading of a note with id $id")
			}
		})
	}

	private fun bind(note: Note) {
		noteId = note.id
		titleField.set(note.title)
		messageField.set(note.body)
		isImportant.set(note.isImportant)
	}

	private fun addNote() {
		repository.insertItem(note, object : DataSource.ItemInsertedCallback {
			override fun onSuccess(id: Long) {
				onNoteAdded.onNext(id)
			}

			override fun onFailure(error: Throwable) {
				Log.w(TAG, error.message ?: "Some error occurred during the insertion of the note to Db")
			}
		})
	}

	private fun switchImportance() {
		val wasImportant = isImportant.get()
		isImportant.set(!wasImportant)
	}

	fun onSubmitClicked() {
		if (isValid) {
			addNote()
		}
	}

	fun onNoteAdded() = onNoteAdded as Observable<Long>
}