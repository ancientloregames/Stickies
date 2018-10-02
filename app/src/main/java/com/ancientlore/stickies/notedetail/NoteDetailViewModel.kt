package com.ancientlore.stickies.notedetail

import android.app.Application
import android.databinding.ObservableField
import android.util.Log
import com.ancientlore.stickies.data.model.Note
import com.ancientlore.stickies.data.source.DataSource
import com.ancientlore.stickies.BasicViewModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class NoteDetailViewModel(application: Application,
						  private val noteId: Long)
	: BasicViewModel(application) {

	companion object {
		private const val TAG = "NoteDetailViewModel"

		const val INTENT_EDIT_NOTE = 101
	}

	val titleField = ObservableField<String>("")
	val messageField = ObservableField<String>("")

	private val editNoteEvent = PublishSubject.create<Long>()

	init { loadNote(noteId) }

	private fun loadNote(id: Long) {
		repository.getItem(id, object : DataSource.ItemLoadedCallback<Note> {
			override fun onSuccess(data: Note) = bind(data)
			override fun onFailure(error: Throwable) {
				Log.w(TAG, error.message ?: "Some error occurred during the loading of a note with id $id")
			}
		})
	}

	private fun bind(note: Note) {
		titleField.set(note.title)
		messageField.set(note.body)
	}

	fun onEditNoteClicked() = editNoteEvent.onNext(noteId)

	fun onEditNote() = editNoteEvent as Observable<Long>
}