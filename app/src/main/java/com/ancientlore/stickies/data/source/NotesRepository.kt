package com.ancientlore.stickies.data.source

import android.util.Log
import com.ancientlore.stickies.data.model.Note
import com.ancientlore.stickies.data.source.local.NotesDao
import com.ancientlore.stickies.data.source.local.NotesLocalSource

object NotesRepository: NotesSource {

	private var localSource: NotesSource? = null

	override fun getAll(callback: DataSource.RequestCallback<List<Note>>) {
		localSource?.getAll(object : DataSource.RequestCallback<List<Note>> {
			override fun onSuccess(result: List<Note>) = callback.onSuccess(result)
			override fun onFailure(error: Throwable) {
				if (error is EmptyResultException)
					Log.w("NotesRepository", "Local database is empty")
				// TODO load from the remote db
			}
		})
	}

	override fun getImportant(callback: DataSource.RequestCallback<List<Note>>) {
		localSource?.getImportant(object : DataSource.RequestCallback<List<Note>> {
			override fun onSuccess(result: List<Note>) = callback.onSuccess(result)
			override fun onFailure(error: Throwable) {
				if (error is EmptyResultException)
					Log.w("NotesRepository", "Local database is empty")
				// TODO load from the remote db
			}
		})
	}

	override fun getItem(id: Long, callback: DataSource.RequestCallback<Note>) {
		localSource?.getItem(id, object : DataSource.RequestCallback<Note> {
			override fun onSuccess(result: Note) = callback.onSuccess(result)
			override fun onFailure(error: Throwable) {
				if (error is EmptyResultException)
					Log.w("NotesRepository", "No item with id $id in the local database")
				// TODO load from the remote db
			}
		})
	}

	override fun insertItem(item: Note, callback: DataSource.RequestCallback<Long>) {
		localSource?.insertItem(item, callback)
	}

	override fun deleteAll() {
		localSource?.deleteAll()
	}

	override fun deleteItem(id: Long) {
		localSource?.deleteItem(id)
	}

	fun initLocalSource(dao: NotesDao) {
		localSource = NotesLocalSource.getInstance(dao)
	}
}