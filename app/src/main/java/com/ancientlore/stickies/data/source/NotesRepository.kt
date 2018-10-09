package com.ancientlore.stickies.data.source

import com.ancientlore.stickies.data.model.Note
import com.ancientlore.stickies.data.source.local.NotesDao
import com.ancientlore.stickies.data.source.local.NotesLocalSource

object NotesRepository: NotesSource {

	private var localSource: NotesSource? = null

	override fun getAll(callback: DataSource.RequestCallback<List<Note>>) {
		localSource?.getAll(callback)
	}

	override fun getImportant(callback: DataSource.RequestCallback<List<Note>>) {
		localSource?.getImportant(callback)
	}

	override fun getItem(id: Long, callback: DataSource.RequestCallback<Note>) {
		localSource?.getItem(id, callback)
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