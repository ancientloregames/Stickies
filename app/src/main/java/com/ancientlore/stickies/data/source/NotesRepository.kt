package com.ancientlore.stickies.data.source

import android.util.Log
import com.ancientlore.stickies.data.model.Note
import com.ancientlore.stickies.data.model.Topic
import com.ancientlore.stickies.data.source.cache.NotesCacheSource
import com.ancientlore.stickies.data.source.local.NotesDao
import com.ancientlore.stickies.data.source.local.NotesLocalSource

object NotesRepository: NotesSource {

	private var cacheSource = NotesCacheSource
	private var localSource: NotesSource? = null

	private var isCacheSynced = false

	override fun getAll(callback: DataSource.RequestCallback<List<Note>>) {
		if (isCacheSynced) {
			callback.onSuccess(cacheSource.getAll())
			return
		}

		localSource?.getAll(object : DataSource.RequestCallback<List<Note>> {
			override fun onSuccess(result: List<Note>) {
				cacheSource.resetWith(result)
				isCacheSynced = true
				callback.onSuccess(result)
			}
			override fun onFailure(error: Throwable) {
				if (error is EmptyResultException)
					Log.w("NotesRepository", "Local database is empty")
				// TODO load from the remote db
				callback.onSuccess(emptyList())
			}
		})
	}

	override fun getImportant(callback: DataSource.RequestCallback<List<Note>>) {
		if (isCacheSynced) {
			callback.onSuccess(cacheSource.getImportant())
			return
		}

		localSource?.getImportant(object : DataSource.RequestCallback<List<Note>> {
			override fun onSuccess(result: List<Note>) = callback.onSuccess(result)
			override fun onFailure(error: Throwable) {
				if (error is EmptyResultException)
					Log.w("NotesRepository", "Local database is empty")
				// TODO load from the remote db
				callback.onSuccess(emptyList())
			}
		})
	}
	override fun getAllByTopic(topic: Topic, callback: DataSource.RequestCallback<List<Note>>) {
		if (isCacheSynced) {
			callback.onSuccess(cacheSource.getAllByTopic(topic))
			return
		}

		localSource?.getAllByTopic(topic, object : DataSource.RequestCallback<List<Note>> {
			override fun onSuccess(result: List<Note>) = callback.onSuccess(result)
			override fun onFailure(error: Throwable) {
				if (error is EmptyResultException)
					Log.w("NotesRepository", "No notes with topic ${topic.name}")
				// TODO load from the remote db
				callback.onSuccess(emptyList())
			}
		})
	}

	override fun getItem(id: Long, callback: DataSource.RequestCallback<Note>) {
		cacheSource.getItem(id)?.let {
			callback.onSuccess(it)
			return@getItem
		}

		localSource?.getItem(id, object : DataSource.RequestCallback<Note> {
			override fun onSuccess(result: Note) {
				cacheSource.insertItem(result)
				callback.onSuccess(result)
			}
			override fun onFailure(error: Throwable) {
				if (error is EmptyResultException)
					Log.w("NotesRepository", "No item with id $id in the local database")
				// TODO load from the remote db
			}
		})
	}

	override fun insertItem(item: Note, callback: DataSource.RequestCallback<Long>) {
		localSource?.insertItem(item, object : DataSource.RequestCallback<Long> {
			override fun onSuccess(result: Long) {
				cacheSource.insertItem(Note.newInstance(result, item))
				callback.onSuccess(result)
			}
			override fun onFailure(error: Throwable) {
				Log.w("NotesRepository", "Item with title ${item.title} wan't been add to the local database")
			}
		})
	}

	override fun updateItem(item: Note) {
		cacheSource.updateItem(item)
		localSource?.updateItem(item)
	}

	override fun reset(newItems: List<Note>) {
		cacheSource.reset(newItems)
		localSource?.reset(newItems)
	}

	override fun deleteAll() {
		cacheSource.deleteAll()
		localSource?.deleteAll()
	}

	override fun deleteItem(id: Long) {
		cacheSource.deleteItem(id)
		localSource?.deleteItem(id)
	}

	override fun switchImportance(id: Long, isImportant: Boolean) {
		cacheSource.switchImportance(id, isImportant)
		localSource?.switchImportance(id, isImportant)
	}

	override fun switchCompletion(id: Long, isCompleted: Boolean) {
		cacheSource.switchComptetion(id, isCompleted)
		localSource?.switchCompletion(id, isCompleted)
	}

	fun initLocalSource(dao: NotesDao) {
		localSource = NotesLocalSource.getInstance(dao)
	}
}