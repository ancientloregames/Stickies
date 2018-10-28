package com.ancientlore.stickies.data.source

import com.ancientlore.stickies.data.model.Note

interface NotesSource: DataSource<Note> {
	fun getImportant(callback: DataSource.RequestCallback<List<Note>>)
	fun switchImportance(id: Long, isImportant: Boolean)
	fun switchCompletion(id: Long, isCompleted: Boolean)
}