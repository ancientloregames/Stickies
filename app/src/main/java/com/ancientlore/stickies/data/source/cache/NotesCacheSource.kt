package com.ancientlore.stickies.data.source.cache

import com.ancientlore.stickies.data.model.Note
import com.ancientlore.stickies.data.model.Topic

object NotesCacheSource {

	private val cache: MutableMap<Long, Note> = HashMap()

	private val notesList get() = cache.values.toList()

	fun getAll() = notesList

	fun getImportant() = notesList.filter { it.isImportant }

	fun getAllByTopic(topic: Topic) = notesList.filter { it.topic == topic.name }

	fun getItem(id: Long) = notesList.find { it.id == id }

	fun insertItem(item: Note) { cache[item.id] = item }

	fun updateItem(item: Note) {
		if (cache.containsKey(item.id)) {
			cache[item.id] = item
		}
	}

	fun reset(newItems: List<Note>) {
		cache.clear()
		newItems.forEach {
			cache[it.id] = it
		}
	}

	fun deleteAll() = cache.clear()

	fun deleteItem(id: Long) = cache.remove(id)

	fun switchImportance(id: Long, isImportant: Boolean) {
		if (cache.containsKey(id)) {
			cache[id]?.isImportant = isImportant
		}
	}

	fun switchComptetion(id: Long, isCompleted: Boolean) {
		if (cache.containsKey(id)) {
			cache[id]?.isCompleted = isCompleted
		}
	}

	fun resetWith(newItems: List<Note>) {
		cache.clear()
		for (item in newItems) {
			cache[item.id] = item
		}
	}
}