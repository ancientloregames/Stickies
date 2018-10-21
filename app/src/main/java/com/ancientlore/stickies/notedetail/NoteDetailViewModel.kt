package com.ancientlore.stickies.notedetail

import android.app.Application
import android.databinding.ObservableField
import android.databinding.ObservableInt
import com.ancientlore.stickies.NotesViewModel
import com.ancientlore.stickies.data.model.Note
import com.ancientlore.stickies.data.source.DataSource
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.text.DateFormat

class NoteDetailViewModel(application: Application,
						  private val noteId: Long)
	: NotesViewModel(application) {

	companion object {
		const val OPTION_DELETE = 0
	}

	val titleField = ObservableField<String>("")
	val messageField = ObservableField<String>("")
	val dateField = ObservableField<String>("")
	val colorField = ObservableInt()

	private val editNoteEvent = PublishSubject.create<Long>()
	private val noteDeletionEvent = PublishSubject.create<Long>()

	init { loadNote(noteId) }

	override fun handleOptionSelection(option: Int): Boolean {
		when (option) {
			OPTION_DELETE -> deleteNote()
			else -> return false
		}
		return true
	}

	fun onEditNoteClicked() = editNoteEvent.onNext(noteId)

	fun observeNoteEditing() = editNoteEvent as Observable<Long>

	fun observeNoteDeletion() = noteDeletionEvent as Observable<Long>

	private fun deleteNote() {
		repository.deleteItem(noteId)
		noteDeletionEvent.onNext(noteId)
	}

	private fun loadNote(id: Long) {
		repository.getItem(id, object : DataSource.SimpleRequestCallback<Note>() {
			override fun onSuccess(result: Note) = bind(result)
		})
	}

	private fun bind(note: Note) {
		colorField.set(note.color)
		titleField.set(note.title)
		messageField.set(note.body)
		dateField.set(note.getDateCreated(DateFormat.SHORT, DateFormat.SHORT))
	}
}