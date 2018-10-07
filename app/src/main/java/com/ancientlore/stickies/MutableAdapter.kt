package com.ancientlore.stickies

import io.reactivex.Observable

interface MutableAdapter<T> {
	fun setItems(newItems: List<T>)
	fun addItem(newItem: T): Boolean
	fun updateItem(updatedItem: T): Boolean
	fun deleteItem(itemToDelete: T): Boolean

	fun sort(@SortOrder order: String)

	fun onItemSelected(): Observable<T>
}