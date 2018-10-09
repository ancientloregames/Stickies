package com.ancientlore.stickies.notedetail

import android.app.Application
import android.databinding.ObservableField
import com.ancientlore.stickies.BasicViewModel
import com.ancientlore.stickies.data.model.Note
import com.ancientlore.stickies.data.source.DataSource
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class NoteDetailViewModel(application: Application,
						  private val noteId: Long)
	: BasicViewModel(application) {

	val titleField = ObservableField<String>("")
	val messageField = ObservableField<String>("")

	private val editNoteEvent = PublishSubject.create<Long>()

	init { loadNote(noteId) }

	private fun loadNote(id: Long) {
		repository.getItem(id, object : DataSource.SimpleRequestCallback<Note>() {
			override fun onSuccess(result: Note) = bind(result)
		})
	}

	private fun bind(note: Note) {
		titleField.set(note.title)
		messageField.set(note.body)
	}

	fun onEditNoteClicked() = editNoteEvent.onNext(noteId)

	fun onEditNote() = editNoteEvent as Observable<Long>
}