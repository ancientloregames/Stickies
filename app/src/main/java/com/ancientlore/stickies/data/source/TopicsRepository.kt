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

	override fun getAll(callback: DataSource.RequestCallback<List<Topic>>) {
		if (isCacheSynced) {
			callback.onSuccess(cacheSource.getAll())
			return
		}

		localSource?.getAll(object : DataSource.RequestCallback<List<Topic>> {
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

	override fun getItem(id: Long, callback: DataSource.RequestCallback<Topic>) {
		cacheSource.getItem(id)?.let {
			callback.onSuccess(it)
			return@getItem
		}

		localSource?.getItem(id, object : DataSource.RequestCallback<Topic> {
			override fun onSuccess(result: Topic) {
				cacheSource.insertItem(result)
				callback.onSuccess(result)
			}
			override fun onFailure(error: Throwable) {
				if (error is EmptyResultException)
					Log.w("TopicsRepository", "No item with id $id in the local database")
				// TODO load from the remote db
			}
		})
	}

	override fun insertItem(item: Topic, callback: DataSource.RequestCallback<Long>) {
		localSource?.insertItem(item, object : DataSource.RequestCallback<Long> {
			override fun onSuccess(result: Long) {
				cacheSource.insertItem(Topic.newInstance(result, item))
				callback.onSuccess(result)
			}
			override fun onFailure(error: Throwable) {
				Log.w("TopicsRepository", "Item with title ${item.title} wan't been add to the local database")
			}
		})
	}

	override fun updateItem(item: Topic) {
		cacheSource.updateItem(item)
		localSource?.updateItem(item)
	}

	override fun deleteAll() {
		cacheSource.deleteAll()
		localSource?.deleteAll()
	}

	override fun deleteItem(id: Long) {
		cacheSource.deleteItem(id)
		localSource?.deleteItem(id)
	}

	fun initLocalSource(dao: TopicsDao) {
		localSource = TopicsLocalSource.getInstance(dao)
	}
}