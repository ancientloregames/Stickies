package com.ancientlore.stickies.data.source

import android.util.Log
import com.ancientlore.stickies.data.model.Topic
import com.ancientlore.stickies.data.source.cache.TopicsCacheSource
import com.ancientlore.stickies.data.source.local.TopicsDao
import com.ancientlore.stickies.data.source.local.TopicsLocalSource

object TopicsRepository: TopicsSource {

	private var cacheSource = TopicsCacheSource
	private var localSource: TopicsSource? = null

	private var isCacheSynced = false

	override fun getAllTopics(callback: DataSource.RequestCallback<List<Topic>>) {
		if (isCacheSynced)
			return callback.onSuccess(cacheSource.getAllTopics())

		localSource?.getAllTopics(object : DataSource.RequestCallback<List<Topic>> {
			override fun onSuccess(result: List<Topic>) {
				cacheSource.resetWith(result)
				isCacheSynced = true
				callback.onSuccess(result)
			}
			override fun onFailure(error: Throwable) {
				if (error is EmptyResultException)
					Log.w("TopicsRepository", "Local database is empty")
				// TODO load from the remote db
				callback.onSuccess(emptyList())
			}
		})
	}

	override fun getTopic(name: String, callback: DataSource.RequestCallback<Topic>) {
		cacheSource.getTopic(name)?.let {
			callback.onSuccess(it)
			return@getTopic
		}

		localSource?.getTopic(name, object : DataSource.RequestCallback<Topic> {
			override fun onSuccess(result: Topic) {
				cacheSource.insertTopic(result)
				callback.onSuccess(result)
			}
			override fun onFailure(error: Throwable) {
				if (error is EmptyResultException)
					Log.w("TopicsRepository", "No item with id $name in the local database")
				// TODO load from the remote db
			}
		})
	}

	override fun insertTopic(topic: Topic) {
		localSource?.insertTopic(topic)
		cacheSource.insertTopic(topic)
	}

	override fun insertTopics(topics: List<Topic>) {
		localSource?.insertTopics(topics)
		cacheSource.insertTopics(topics)
	}

	override fun deleteAllTopics() {
		localSource?.deleteAllTopics()
		cacheSource.deleteAllTopics()
	}

	override fun deleteTopic(name: String) {
		localSource?.deleteTopic(name)
		cacheSource.deleteTopic(name)
	}

	fun initLocalSource(dao: TopicsDao) {
		localSource = TopicsLocalSource.getInstance(dao)
	}
}