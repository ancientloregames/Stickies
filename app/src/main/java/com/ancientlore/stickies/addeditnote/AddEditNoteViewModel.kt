package com.ancientlore.stickies.addeditnote

import android.app.Application
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import com.ancientlore.stickies.BasicViewModel
import com.ancientlore.stickies.data.model.Note
import com.ancientlore.stickies.data.source.DataSource
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class AddEditNoteViewModel(application: Application): BasicViewModel(application) {

	companion object {
		const val OPTION_IMPRTANT = "option_important"

		private const val NOTE_VALID = 0
		const val ALERT_TITLE_EMPTY = 1
		const val ALERT_TITLE_LONG = 2
		const val ALERT_BODY_LONG = 3

		const val TITLE_LIMIT = 256
		const val BODY_LIMIT = 2048
	}

	val titleField = ObservableField<String>("")
	val messageField = ObservableField<String>("")
	val isImportant = ObservableBoolean(false)

	private var editedNote: Note? = null

	private val onNoteAdded = PublishSubject.create<Long>()
	private val onAlert = PublishSubject.create<Int>()

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

	fun onSubmitClicked() {
		if (isValid())
			addNote()
	}

	fun observeNoteAdded() = onNoteAdded as Observable<Long>

	fun observeAlert() = onAlert as Observable<Int>

	private fun loadNote(id: Long) {
		repository.getItem(id, object : DataSource.SimpleRequestCallback<Note>() {
			override fun onSuccess(result: Note) = bind(result)
		})
	}

	private fun bind(note: Note) {
		editedNote = note
		titleField.set(note.title)
		messageField.set(note.body)
		isImportant.set(note.isImportant)
	}

	private fun addNote() {
		repository.insertItem(composeNote(), object : DataSource.SimpleRequestCallback<Long>() {
			override fun onSuccess(result: Long) = onNoteAdded.onNext(result)
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

	private fun isValid(): Boolean {
		val title = titleField.get()!!
		val body = messageField.get()!!
		val messageId = when {
			title.isEmpty() -> ALERT_TITLE_EMPTY
			title.length > TITLE_LIMIT -> ALERT_TITLE_LONG
			body.length > BODY_LIMIT -> ALERT_BODY_LONG
			else -> NOTE_VALID
		}

		return if (messageId != NOTE_VALID) {
			onAlert.onNext(messageId)
			false
		} else true
	}
}