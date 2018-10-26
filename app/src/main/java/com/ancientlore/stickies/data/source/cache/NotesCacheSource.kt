package com.ancientlore.stickies.data.source.cache

import com.ancientlore.stickies.data.model.Note

object NotesCacheSource {

	private val cache: MutableMap<Long, Note> = HashMap()

	private val notesList get() = cache.values.toList()

	fun getAll() = notesList

	fun getImportant() = notesList.filter { it.isImportant }

	fun getItem(id: Long) = notesList.find { it.id == id }

	fun insertItem(item: Note) { cache[item.id] = item }

	fun updateItem(item: Note) {
		if (cache.containsKey(item.id)) {
			cache[item.id] = item
		}
	}

	fun deleteAll() = cache.clear()

	fun deleteItem(id: Long) = cache.remove(id)

	fun resetWith(newItems: List<Note>) {
		cache.clear()
		for (item in newItems) {
			cache[item.id] = item
		}
	}
}