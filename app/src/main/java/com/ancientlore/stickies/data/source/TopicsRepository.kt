package com.ancientlore.stickies.data.source

import android.util.Log
import com.ancientlore.stickies.data.model.Topic
import com.ancientlore.stickies.data.source.cache.TopicsCacheSource
import com.ancientlore.stickies.data.source.local.TopicsDao
import com.ancientlore.stickies.data.source.local.TopicsLocalSource
import com.ancientlore.stickies.data.source.remote.FirestoreTopicsSource
import java.lang.RuntimeException

object TopicsRepository: TopicsSource {

	private const val TAG = "TopicsRepository"

	private var cacheSource = TopicsCacheSource
	private var localSource: TopicsSource? = null
	private var remoteSource: TopicsSource? = null

	private var isCacheSynced = false

	override fun getAllTopics(callback: DataSource.RequestCallback<List<Topic>>) {
		if (isCacheSynced)
			return callback.onSuccess(cacheSource.getAllTopics())

		localSource?.getAllTopics(object : DataSource.RequestCallback<List<Topic>> {
			override fun onSuccess(result: List<Topic>) {
				resetCache(result)
				callback.onSuccess(result)
			}
			override fun onFailure(error: Throwable) {
				error.printStackTrace()
				onEmptyLocalSource()
			}
		})
	}

	override fun getTopic(name: String, callback: DataSource.RequestCallback<Topic>) {
		cacheSource.getTopic(name)?.let {
			callback.onSuccess(it)
			return
		}

		localSource?.getTopic(name, object : DataSource.RequestCallback<Topic> {
			override fun onSuccess(result: Topic) {
				cacheSource.insertTopic(result)
				callback.onSuccess(result)
			}
			override fun onFailure(error: Throwable) {
				error.printStackTrace()
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

	override fun reset(newTopics: List<Topic>) {
		localSource?.reset(newTopics)
		remoteSource?.reset(newTopics)
		resetCache(newTopics)
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

	fun initRemoteSource(userId: String) {
		remoteSource = FirestoreTopicsSource.getInstance(userId)
	}

	private fun resetCache(newTopics: List<Topic>) {
		cacheSource.reset(newTopics)
		isCacheSynced = true
	}

	private fun onEmptyLocalSource() {
		getAllRemotely(object : DataSource.RequestCallback<List<Topic>> {
			override fun onSuccess(result: List<Topic>) {
				Log.d(TAG, "Got ${result.size} topics from the remote source")
				localSource?.reset(result)
				resetCache(result)
			}
			override fun onFailure(error: Throwable) = error.printStackTrace()
		})
	}

	private fun getAllRemotely(callback: DataSource.RequestCallback<List<Topic>>) {
		remoteSource?.getAllTopics(callback)
			?: callback.onFailure(RuntimeException("No remote source attached to the repository"))
	}
}