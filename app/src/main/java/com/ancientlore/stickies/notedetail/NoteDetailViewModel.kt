package com.ancientlore.stickies.notedetail

import android.app.Application
import android.databinding.ObservableField
import android.util.Log
import com.ancientlore.stickies.data.model.Note
import com.ancientlore.stickies.data.source.DataSource
import com.ancientlore.stickies.viewmodel.BasicViewModel

class NoteDetailViewModel(application: Application, noteId: Long): BasicViewModel(application) {

	companion object {
		private const val TAG = "NoteDetailViewModel"
	}

	val titleField = ObservableField<String>("")
	val messageField = ObservableField<String>("")

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
}