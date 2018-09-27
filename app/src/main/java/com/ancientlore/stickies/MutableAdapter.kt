package com.ancientlore.stickies

interface MutableAdapter<T> {
	fun setItems(newItems: List<T>)
	fun addItem(newItem: T): Boolean
	fun updateItem(updatedItem: T): Boolean
	fun deleteItem(itemToDelete: T): Boolean
}