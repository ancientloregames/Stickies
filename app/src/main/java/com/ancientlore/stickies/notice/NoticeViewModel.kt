package com.ancientlore.stickies.notice

import android.app.Application
import android.databinding.ObservableField
import com.ancientlore.stickies.NotesViewModel
import com.ancientlore.stickies.data.model.Note
import com.ancientlore.stickies.data.source.DataSource
import com.ancientlore.stickies.utils.getTitle

class NoticeViewModel(application: Application, noteId: Long): NotesViewModel(application) {

	val titleField = ObservableField<String>("")

	init { loadNote(noteId) }

	private fun loadNote(id: Long) {
		repository.getItem(id, object : DataSource.SimpleRequestCallback<Note>() {
			override fun onSuccess(result: Note) = bind(result)
		})
	}

	private fun bind(note: Note) {
		titleField.set(note.getTitle(context))
	}
}