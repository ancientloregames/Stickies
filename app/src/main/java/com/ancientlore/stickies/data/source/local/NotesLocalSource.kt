package com.ancientlore.stickies.data.source.local

import com.ancientlore.stickies.SingletonHolder
import com.ancientlore.stickies.data.model.Note
import com.ancientlore.stickies.data.source.DataSource
import com.ancientlore.stickies.data.source.NotesSource
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class NotesLocalSource private constructor(private val dao: NotesDao)
	: NotesSource {

	private val executor: ExecutorService = Executors.newSingleThreadExecutor { r -> Thread(r, "db_worker") }

	override fun getAll(callback: DataSource.ListLoadedCallback<Note>) {
		executor.submit {
			dao.getAll()
					.takeIf { it.isNotEmpty() }
					?.let { callback.onSuccess(it) }
					?: callback.onFailure(Throwable("Database is empty"))
		}
	}

	override fun getItem(id: Long, callback: DataSource.ItemLoadedCallback<Note>) {
		executor.submit {
			dao.findById(id)
					?.let { callback.onSuccess(it) }
					?: callback.onFailure(Throwable("No such item"))
		}
	}

	override fun insertItem(item: Note, callback: DataSource.ItemInsertedCallback) {
		executor.submit {
			dao.insert(item)
					.let { callback.onSuccess(it) }
		}
	}

	override fun deleteAll() {
		executor.submit {
			dao.deleteAll()
		}
	}

	override fun deleteItem(id: Long) {
		executor.submit {
			dao.deleteById(id)
		}
	}

	internal companion object : SingletonHolder<NotesLocalSource, NotesDao>({
		NotesLocalSource(it)
	})
}