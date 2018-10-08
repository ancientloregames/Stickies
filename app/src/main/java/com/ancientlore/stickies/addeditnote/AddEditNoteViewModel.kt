package com.ancientlore.stickies.addeditnote

import android.app.Application
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.util.Log
import com.ancientlore.stickies.BasicViewModel
import com.ancientlore.stickies.data.model.Note
import com.ancientlore.stickies.data.source.DataSource
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class AddEditNoteViewModel(application: Application): BasicViewModel(application) {

	companion object {
		private const val TAG = "NoteActivityViewModel"

		const val OPTION_IMPRTANT = "option_important"
	}

	val titleField = ObservableField<String>("")
	val messageField = ObservableField<String>("")
	val isImportant = ObservableBoolean(false)

	private var editedNote: Note? = null

	private val isValid get() = titleField.get()?.isNotEmpty() ?: false

	private val onNoteAdded = PublishSubject.create<Long>()

	constructor(application: Application, noteId: Long) : this(application) {
		loadNote(noteId)
	}

	override fun handleOptionSelection(option: String): Boolean {
		when (option) {
			OPTION_IMPRTANT -> switchImportance()
			else -> return false
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
		editedNote = note
		titleField.set(note.title)
		messageField.set(note.body)
		isImportant.set(note.isImportant)
	}

	private fun addNote() {
		repository.insertItem(composeNote(), object : DataSource.ItemInsertedCallback {
			override fun onSuccess(id: Long) = onNoteAdded.onNext(id)
			override fun onFailure(error: Throwable) {
				Log.w(TAG, error.message ?: "Some error occurred during the insertion of the note to Db")
			}
		})
	}

	private fun switchImportance() {
		val wasImportant = isImportant.get()
		isImportant.set(!wasImportant)
	}

	private fun composeNote() = editedNote?.let { composeNoteBasedOn(it) } ?: composeNewNote()

	private fun composeNewNote() = Note(
			timeCreated = System.currentTimeMillis(),
			title = titleField.get()!!,
			body = messageField.get()!!,
			isImportant = isImportant.get())

	private fun composeNoteBasedOn(note: Note) = Note(
			id = note.id,
			timeCreated = note.timeCreated,
			timeUpdated = System.currentTimeMillis(),
			timeNotify = note.timeCreated,
			title = titleField.get()!!,
			body = messageField.get()!!,
			color = note.color,
			icon = note.icon,
			topic = note.topic,
			isImportant = isImportant.get(),
			isCompleted = note.isCompleted)

	fun onSubmitClicked() {
		if (isValid) {
			addNote()
		}
	}

	fun onNoteAdded() = onNoteAdded as Observable<Long>
}