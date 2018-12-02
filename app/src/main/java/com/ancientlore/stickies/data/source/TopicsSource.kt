package com.ancientlore.stickies.data.source

import com.ancientlore.stickies.data.model.Topic

interface TopicsSource {

	fun getAllTopics(callback: DataSource.RequestCallback<List<Topic>>)

	fun getTopic(name: String, callback: DataSource.RequestCallback<Topic>)

	fun insertTopic(topic: Topic)

	fun insertTopics(topics: List<Topic>)

	fun deleteTopic(name: String)

	fun deleteAllTopics()
}