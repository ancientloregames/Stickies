package com.ancientlore.stickies.data.source

import android.util.Log
import com.ancientlore.stickies.data.model.Note
import com.ancientlore.stickies.data.model.Topic
import com.ancientlore.stickies.data.source.cache.NotesCacheSource
import com.ancientlore.stickies.data.source.local.NotesDao
import com.ancientlore.stickies.data.source.local.NotesLocalSource
import com.ancientlore.stickies.data.source.remote.FirestoreNotesSource
import com.google.firebase.auth.FirebaseUser

object NotesRepository: NotesSource {

	private var cacheSource = NotesCacheSource
	private var localSource: NotesSource? = null
	private var remoteSource: FirestoreNotesSource? = null

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
				else error.printStackTrace()
				getAllRemotely(callback)
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
					Log.w("NotesRepository", "No important notes in the local database")
				getImportantRemotely(callback)
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
					Log.w("NotesRepository", "No notes with a topic ${topic.name} in the local database")
				getAllByTopicRemotely(topic, callback)
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
				getItemRemotly(id, callback)
			}
		})
	}

	override fun insertItem(item: Note, callback: DataSource.RequestCallback<Long>) {
		localSource?.insertItem(item, object : DataSource.RequestCallback<Long> {
			override fun onSuccess(result: Long) {
				onInsertedLocaly(Note.newInstance(finalId = result, note = item))
				callback.onSuccess(result)
			}
			override fun onFailure(error: Throwable) {
				Log.w("NotesRepository", "Item with title ${item.title} hasn't been added to the local database")
			}
		})
	}

	override fun updateItem(item: Note) {
		cacheSource.updateItem(item)
		localSource?.updateItem(item)
		remoteSource?.updateItem(item)
	}

	override fun reset(newItems: List<Note>) {
		cacheSource.reset(newItems)
		localSource?.reset(newItems)
		remoteSource?.reset(newItems)
	}

	override fun deleteAll() {
		cacheSource.deleteAll()
		localSource?.deleteAll()
		remoteSource?.deleteAll()
	}

	override fun deleteItem(id: Long) {
		cacheSource.deleteItem(id)
		localSource?.deleteItem(id)
		remoteSource?.deleteItem(id)
	}

	override fun switchImportance(id: Long, isImportant: Boolean) {
		cacheSource.switchImportance(id, isImportant)
		localSource?.switchImportance(id, isImportant)
		remoteSource?.switchImportance(id, isImportant)
	}

	override fun switchCompletion(id: Long, isCompleted: Boolean) {
		cacheSource.switchComptetion(id, isCompleted)
		localSource?.switchCompletion(id, isCompleted)
	}

	fun initLocalSource(dao: NotesDao) {
		localSource = NotesLocalSource.getInstance(dao)
	}

	fun initRemoteSource(user: FirebaseUser) {
		remoteSource = FirestoreNotesSource.getInstance(user)
	}

	private fun getAllRemotely(callback: DataSource.RequestCallback<List<Note>>) {
		remoteSource?.getAll(object : DataSource.RequestCallback<List<Note>> {
			override fun onSuccess(result: List<Note>) {
				localSource?.reset(result)
				cacheSource.resetWith(result)
				isCacheSynced = true
				callback.onSuccess(result)
			}
			override fun onFailure(error: Throwable) {
				if (error is EmptyResultException)
					Log.w("NotesRepository", "Remote database is empty")
				else error.printStackTrace()
				callback.onSuccess(emptyList())
			}
		})
	}

	private fun getImportantRemotely(callback: DataSource.RequestCallback<List<Note>>) {
		remoteSource?.getImportant(object : DataSource.RequestCallback<List<Note>> {
			override fun onSuccess(result: List<Note>) = callback.onSuccess(result)
			override fun onFailure(error: Throwable) {
				if (error is EmptyResultException)
					Log.w("NotesRepository", "No important notes in the remote database")
				else error.printStackTrace()
				callback.onSuccess(emptyList())
			}
		})
	}

	private fun getAllByTopicRemotely(topic: Topic, callback: DataSource.RequestCallback<List<Note>>) {
		remoteSource?.getAllByTopic(topic, object : DataSource.RequestCallback<List<Note>> {
			override fun onSuccess(result: List<Note>) = callback.onSuccess(result)
			override fun onFailure(error: Throwable) {
				if (error is EmptyResultException)
					Log.w("NotesRepository", "No notes with a topic ${topic.name} in the remote database")
				else error.printStackTrace()
				callback.onSuccess(emptyList())
			}
		})
	}

	private fun onInsertedLocaly(item: Note) {
		cacheSource.insertItem(item)
		remoteSource?.insertItem(item, object : DataSource.RequestCallback<Long> {
			override fun onSuccess(result: Long) {
				Log.d("NotesRepository", "Item with title ${item.title} has been added to the remote database")
			}
			override fun onFailure(error: Throwable) {
				Log.w("NotesRepository", "Item with title ${item.title} hasn't been added to the remote database")
			}
		})
	}

	private fun getItemRemotly(id: Long, callback: DataSource.RequestCallback<Note>) {
		Log.w("NotesRepository", "Getting item with id $id from the remote database")
		remoteSource?.getItem(id, object : DataSource.RequestCallback<Note> {
			override fun onSuccess(result: Note) {
				cacheSource.insertItem(result)
				callback.onSuccess(result)
			}
			override fun onFailure(error: Throwable) {
				if (error is EmptyResultException)
					Log.w("NotesRepository", "No item with id $id in the remote database")
			}
		})
	}
}