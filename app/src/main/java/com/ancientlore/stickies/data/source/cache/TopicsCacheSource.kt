package com.ancientlore.stickies.data.source.cache

import com.ancientlore.stickies.data.model.Topic

object TopicsCacheSource {

	private val cache: MutableMap<Long, Topic> = HashMap()

	private val notesList get() = cache.values.toList()

	fun getAll() = notesList

	fun getItem(id: Long) = notesList.find { it.id == id }

	fun insertItem(item: Topic) { cache[item.id] = item }

	fun updateItem(item: Topic) {
		if (cache.containsKey(item.id)) {
			cache[item.id] = item
		}
	}

	fun deleteAll() = cache.clear()

	fun deleteItem(id: Long) = cache.remove(id)

	fun resetWith(newItems: List<Topic>) {
		cache.clear()
		for (item in newItems) {
			cache[item.id] = item
		}
	}
}