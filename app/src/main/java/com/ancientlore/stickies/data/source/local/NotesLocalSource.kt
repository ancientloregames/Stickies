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

	internal companion object : SingletonHolder<NotesLocalSource, NotesDao>({ NotesLocalSource(it) }) {
		private const val TAG = "FirestoreNotesSource"
	}

	private val executor: ExecutorService = Executors.newSingleThreadExecutor { r -> Thread(r, "db_worker") }

	override fun getAll(callback: DataSource.RequestCallback<List<Note>>) {
		executor.submit {
			dao.getAll()
					.takeIf { it.isNotEmpty() }
					?.let { callback.onSuccess(it) }
					?: callback.onFailure(EmptyResultException("$TAG: empty"))
		}
	}

	override fun getImportant(callback: DataSource.RequestCallback<List<Note>>) {
		executor.submit {
			dao.getImportant()
					.takeIf { it.isNotEmpty() }
					?.let { callback.onSuccess(it) }
					?: callback.onFailure(EmptyResultException("$TAG: no important notes"))
		}
	}

	override fun getAllByTopic(topic: Topic, callback: DataSource.RequestCallback<List<Note>>) {
		executor.submit {
			dao.getAllByTopic(topic.name)
					.takeIf { it.isNotEmpty() }
					?.let { callback.onSuccess(it) }
					?: callback.onFailure(EmptyResultException("$TAG: no items with the topic ${topic.name}"))
		}
	}

	override fun getItem(id: Long, callback: DataSource.RequestCallback<Note>) {
		executor.submit {
			dao.findById(id)
					?.let { callback.onSuccess(it) }
					?: callback.onFailure(EmptyResultException("$TAG: no item with the id $id"))
		}
	}

	override fun insertItem(item: Note, callback: DataSource.RequestCallback<Long>?) {
		executor.submit {
			dao.insert(item)
					.let { callback?.onSuccess(it) }
					?: callback?.onFailure(EmptyResultException("$TAG: Item with title ${item.title} hasn't been added"))
		}
	}

	override fun insertItems(items: List<Note>, callback: DataSource.RequestCallback<LongArray>?) {
		executor.submit {
			dao.insert(items)
					.let { callback?.onSuccess(it) }
					?: callback?.onFailure(EmptyResultException("$TAG: ${items.size} items haven't been added"))
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