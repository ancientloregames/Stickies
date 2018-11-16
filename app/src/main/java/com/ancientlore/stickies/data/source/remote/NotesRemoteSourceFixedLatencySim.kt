package com.ancientlore.stickies.data.source.remote

import com.ancientlore.stickies.data.model.Note
import com.ancientlore.stickies.data.model.Topic
import com.ancientlore.stickies.data.source.DataSource
import com.ancientlore.stickies.data.source.EmptyResultException
import com.ancientlore.stickies.data.source.NotesSource
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object NotesRemoteSourceFixedLatencySim : NotesSource {

	private const val LATENCY_MILLIS = 2000L

	private val executor = Executors.newSingleThreadScheduledExecutor { r -> Thread(r, "remote_db_worker") }

	private val data = HashMap<Long, Note>(7).apply {
		put(1, Note(id = 1, title = "title 1"))
		put(2, Note(id = 2, body = "body 2"))
		put(3, Note(id = 3, title = "title 3", body = "body 3"))
		put(4, Note(id = 4, title = "important 4", isImportant = true))
		put(5, Note(id = 5, title = "completed 5", isCompleted = true))
		put(6, Note(id = 6, title = "important and completed 6", isImportant = true, isCompleted = true))
		put(7, Note(id = 7, title = "updated 7", timeUpdated = System.currentTimeMillis() + 2000))
	}

	private val notesList get() = data.values.toList()

	override fun getAll(callback: DataSource.RequestCallback<List<Note>>) {
		executor.schedule({
			callback.onSuccess(notesList)
		}, LATENCY_MILLIS, TimeUnit.MILLISECONDS)
	}

	override fun getImportant(callback: DataSource.RequestCallback<List<Note>>) {
		executor.schedule({
			callback.onSuccess(notesList.filter { it.isImportant })
		}, LATENCY_MILLIS, TimeUnit.MILLISECONDS)
	}

	override fun getAllByTopic(topic: Topic, callback: DataSource.RequestCallback<List<Note>>) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun getItem(id: Long, callback: DataSource.RequestCallback<Note>) {
		executor.schedule({
			notesList.find { it.id == id }
					?.let { callback.onSuccess(it) }
					?: callback.onFailure(EmptyResultException())
		}, LATENCY_MILLIS, TimeUnit.MILLISECONDS)
	}

	override fun insertItem(item: Note, callback: DataSource.RequestCallback<Long>) {
		executor.schedule({
			val id = data.size.toLong()
			data[id] = item
			callback.onSuccess(id)
		}, LATENCY_MILLIS, TimeUnit.MILLISECONDS)
	}

	override fun updateItem(item: Note) {
		executor.schedule({
			if (data.containsKey(item.id)) {
				data[item.id] = item
			}
		}, LATENCY_MILLIS, TimeUnit.MILLISECONDS)
	}

	override fun deleteAll() {
		executor.schedule({
			data.clear()
		}, LATENCY_MILLIS, TimeUnit.MILLISECONDS)
	}

	override fun deleteItem(id: Long) {
		executor.schedule({
			data.remove(id)
		}, LATENCY_MILLIS, TimeUnit.MILLISECONDS)
	}

	override fun switchImportance(id: Long, isImportant: Boolean) {
		executor.schedule({
			if (data.containsKey(id)) {
				data[id]?.isImportant = isImportant
			}
		}, LATENCY_MILLIS, TimeUnit.MILLISECONDS)
	}

	override fun switchCompletion(id: Long, isCompleted: Boolean) {
		executor.schedule({
			if (data.containsKey(id)) {
				data[id]?.isCompleted = isCompleted
			}
		}, LATENCY_MILLIS, TimeUnit.MILLISECONDS)
	}
}