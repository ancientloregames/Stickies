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

	override fun getAllTopics(callback: DataSource.RequestCallback<List<Topic>>) {
		executor.submit {
			dao.getAll()
					.takeIf { it.isNotEmpty() }
					?.let { callback.onSuccess(it) }
					?: callback.onFailure(EmptyResultException())
		}
	}

	override fun getTopic(name: String, callback: DataSource.RequestCallback<Topic>) {
		executor.submit {
			dao.findById(name)
					?.let { callback.onSuccess(it) }
					?: callback.onFailure(EmptyResultException())
		}
	}

	override fun insertTopic(topic: Topic) {
		executor.submit {
			dao.insert(topic)
		}
	}

	override fun deleteAllTopics() {
		executor.submit {
			dao.deleteAll()
		}
	}

	override fun deleteTopic(name: String) {
		executor.submit {
			dao.deleteById(name)
		}
	}
}