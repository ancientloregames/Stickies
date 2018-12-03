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

	private const val TAG = "NotesRepository"

	private var cacheSource = NotesCacheSource
	private var localSource: NotesSource? = null
	private var remoteSource: NotesSource? = null

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
				error.printStackTrace()
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
				error.printStackTrace()
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
				error.printStackTrace()
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
				error.printStackTrace()
				getItemRemotly(id, callback)
			}
		})
	}

	override fun insertItem(item: Note, callback: DataSource.RequestCallback<Long>?) {
		localSource?.insertItem(item, object : DataSource.RequestCallback<Long> {
			override fun onSuccess(result: Long) {
				onInsertedLocaly(Note.newInstance(finalId = result, note = item))
				callback?.onSuccess(result)
			}
			override fun onFailure(error: Throwable) = error.printStackTrace()
		})
	}

	override fun insertItems(items: List<Note>, callback: DataSource.RequestCallback<LongArray>?) {
		localSource?.insertItems(items, object : DataSource.RequestCallback<LongArray> {
			override fun onSuccess(result: LongArray) {
				for (i in 0 until items.size)
					items[i].id = result[i]
				onInsertedLocaly(items)
				callback?.onSuccess(result)
			}
			override fun onFailure(error: Throwable) = error.printStackTrace()
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
		remoteSource?.switchCompletion(id, isCompleted)
	}

	fun initLocalSource(dao: NotesDao) {
		localSource = NotesLocalSource.getInstance(dao)
	}

	fun initRemoteSource(user: FirebaseUser) {
		remoteSource = FirestoreNotesSource.getInstance(user)
	}

	fun getAllRemotely(callback: DataSource.RequestCallback<List<Note>>) {
		remoteSource?.getAll(object : DataSource.RequestCallback<List<Note>> {
			override fun onSuccess(result: List<Note>) {
				syncLocalWithRemote(result)
				callback.onSuccess(result)
			}
			override fun onFailure(error: Throwable) {
				error.printStackTrace()
				callback.onSuccess(emptyList())
			}
		})
	}

	fun syncLocalWithRemote(remoteNotes: List<Note>) {
		localSource?.getAll(object : DataSource.SimpleRequestCallback<List<Note>>() {
			override fun onSuccess(result: List<Note>) {
				remoteSource?.insertItems(result)
			}
		})
		localSource?.insertItems(remoteNotes)
		cacheSource.insertUniqueItems(remoteNotes)
		isCacheSynced = true
	}

	private fun getImportantRemotely(callback: DataSource.RequestCallback<List<Note>>) {
		remoteSource?.getImportant(object : DataSource.RequestCallback<List<Note>> {
			override fun onSuccess(result: List<Note>) = callback.onSuccess(result)
			override fun onFailure(error: Throwable) {
				error.printStackTrace()
				callback.onSuccess(emptyList())
			}
		})
	}

	private fun getAllByTopicRemotely(topic: Topic, callback: DataSource.RequestCallback<List<Note>>) {
		remoteSource?.getAllByTopic(topic, object : DataSource.RequestCallback<List<Note>> {
			override fun onSuccess(result: List<Note>) = callback.onSuccess(result)
			override fun onFailure(error: Throwable) {
				error.printStackTrace()
				callback.onSuccess(emptyList())
			}
		})
	}

	private fun onInsertedLocaly(item: Note) {
		cacheSource.insertItem(item)
		remoteSource?.insertItem(item, object : DataSource.RequestCallback<Long> {
			override fun onSuccess(result: Long) {
				Log.d(TAG, "Item with title ${item.title} has been added to the remote database")
			}
			override fun onFailure(error: Throwable) {
				Log.w(TAG, "Item with title ${item.title} hasn't been added to the remote database", error)
			}
		})
	}

	private fun onInsertedLocaly(items: List<Note>) {
		items.forEach { cacheSource.insertItem(it) }
		remoteSource?.insertItems(items, object : DataSource.RequestCallback<LongArray> {
			override fun onSuccess(result: LongArray) {
				Log.d(TAG, "Items have been added to the remote database")
			}
			override fun onFailure(error: Throwable) {
				Log.w(TAG, "Items haven't been added to the remote database", error)
			}
		})
	}

	private fun getItemRemotly(id: Long, callback: DataSource.RequestCallback<Note>) {
		Log.w(TAG, "Getting item with id $id from the remote database")
		remoteSource?.getItem(id, object : DataSource.RequestCallback<Note> {
			override fun onSuccess(result: Note) {
				cacheSource.insertItem(result)
				localSource?.insertItem(result)
				callback.onSuccess(result)
			}
			override fun onFailure(error: Throwable) = error.printStackTrace()
		})
	}
}