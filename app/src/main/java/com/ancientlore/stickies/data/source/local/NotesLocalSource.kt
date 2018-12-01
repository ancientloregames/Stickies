package com.ancientlore.stickies.data.source.local

import com.ancientlore.stickies.SingletonHolder
import com.ancientlore.stickies.data.model.Note
import com.ancientlore.stickies.data.model.Topic
import com.ancientlore.stickies.data.source.DataSource
import com.ancientlore.stickies.data.source.EmptyResultException
import com.ancientlore.stickies.data.source.NotesSource
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class NotesLocalSource private constructor(private val dao: NotesDao)
	: NotesSource {

	internal companion object : SingletonHolder<NotesLocalSource, NotesDao>({ NotesLocalSource(it) })

	private val executor: ExecutorService = Executors.newSingleThreadExecutor { r -> Thread(r, "db_worker") }

	override fun getAll(callback: DataSource.RequestCallback<List<Note>>) {
		executor.submit {
			dao.getAll()
					.takeIf { it.isNotEmpty() }
					?.let { callback.onSuccess(it) }
					?: callback.onFailure(EmptyResultException())
		}
	}

	override fun getImportant(callback: DataSource.RequestCallback<List<Note>>) {
		executor.submit {
			dao.getImportant()
					.takeIf { it.isNotEmpty() }
					?.let { callback.onSuccess(it) }
					?: callback.onFailure(EmptyResultException())
		}
	}

	override fun getAllByTopic(topic: Topic, callback: DataSource.RequestCallback<List<Note>>) {
		executor.submit {
			dao.getAllByTopic(topic.name)
					.takeIf { it.isNotEmpty() }
					?.let { callback.onSuccess(it) }
					?: callback.onFailure(EmptyResultException())
		}
	}

	override fun getItem(id: Long, callback: DataSource.RequestCallback<Note>) {
		executor.submit {
			dao.findById(id)
					?.let { callback.onSuccess(it) }
					?: callback.onFailure(EmptyResultException())
		}
	}

	override fun insertItem(item: Note, callback: DataSource.RequestCallback<Long>?) {
		executor.submit {
			dao.insert(item)
					.let { callback?.onSuccess(it) }
		}
	}

	override fun updateItem(item: Note) {
		executor.submit {
			dao.update(item)
		}
	}

	override fun reset(newItems: List<Note>) {
		executor.submit {
			dao.deleteAll()
			dao.insert(newItems)
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

	override fun switchImportance(id: Long, isImportant: Boolean) {
		executor.submit {
			dao.switchImportance(id, isImportant)
		}
	}

	override fun switchCompletion(id: Long, isCompleted: Boolean) {
		executor.submit {
			dao.switchImportance(id, isCompleted)
		}
	}
}