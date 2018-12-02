package com.ancientlore.stickies.data.source.cache

import com.ancientlore.stickies.data.model.Topic

object TopicsCacheSource {

	private val cache: MutableMap<String, Topic> = HashMap()

	private val topicsList get() = cache.values.toList()

	fun getAllTopics() = topicsList

	fun getTopic(name: String) = topicsList.find { it.name == name }

	fun insertTopic(item: Topic) { cache[item.name] = item }

	fun insertTopics(topics: List<Topic>) = topics.forEach { insertTopic(it) }

	fun deleteAllTopics() = cache.clear()

	fun deleteTopic(title: String) = cache.remove(title)

	fun reset(newItems: List<Topic>) {
		cache.clear()
		for (item in newItems)
			cache[item.name] = item
	}
}