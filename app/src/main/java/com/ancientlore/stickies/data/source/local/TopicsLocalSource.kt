package com.ancientlore.stickies.data.source.local

import com.ancientlore.stickies.SingletonHolder
import com.ancientlore.stickies.data.source.TopicsSource
import com.ancientlore.stickies.data.model.Topic
import com.ancientlore.stickies.data.source.DataSource
import com.ancientlore.stickies.data.source.EmptyResultException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TopicsLocalSource private constructor(private val dao: TopicsDao)
	: TopicsSource {

	internal companion object : SingletonHolder<TopicsLocalSource, TopicsDao>({ TopicsLocalSource(it) })

	private val executor: ExecutorService = Executors.newSingleThreadExecutor { r -> Thread(r, "db_worker_topic") }

	override fun getAll(callback: DataSource.RequestCallback<List<Topic>>) {
		executor.submit {
			dao.getAll()
					.takeIf { it.isNotEmpty() }
					?.let { callback.onSuccess(it) }
					?: callback.onFailure(EmptyResultException())
		}
	}

	override fun getItem(id: Long, callback: DataSource.RequestCallback<Topic>) {
		executor.submit {
			dao.findById(id)
					?.let { callback.onSuccess(it) }
					?: callback.onFailure(EmptyResultException())
		}
	}

	override fun insertItem(item: Topic, callback: DataSource.RequestCallback<Long>) {
		executor.submit {
			dao.insert(item)
					.let { callback.onSuccess(it) }
		}
	}

	override fun updateItem(item: Topic) {
		executor.submit {
			dao.update(item)
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
}