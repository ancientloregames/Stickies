package com.ancientlore.stickies.data.source

import com.ancientlore.stickies.data.model.Note

interface NotesSource: DataSource<Note> {
	fun getImportant(callback: DataSource.RequestCallback<List<Note>>)
	fun switchImportance(id: Long, isImportant: Boolean)
	fun switchCompletion(id: Long, isCompleted: Boolean)

//	fun getAllTopics(callback: DataSource.RequestCallback<List<Topic>>)
//	fun getTopic(id: Long, callback: DataSource.RequestCallback<Topic>)
//	fun insertTopic(topic: Topic, callback: DataSource.RequestCallback<Long>)
//	fun updateTopic(topic: Topic)
//	fun deleteAllTopics()
//	fun deleteTopic(id: Long)
}