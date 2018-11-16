package com.ancientlore.stickies.data.source

import com.ancientlore.stickies.data.model.Note
import com.ancientlore.stickies.data.model.Topic

interface NotesSource: DataSource<Note> {
	fun getImportant(callback: DataSource.RequestCallback<List<Note>>)
	fun getAllByTopic(topic: Topic, callback: DataSource.RequestCallback<List<Note>>)
	fun switchImportance(id: Long, isImportant: Boolean)
	fun switchCompletion(id: Long, isCompleted: Boolean)
}