package com.ancientlore.stickies

import android.support.annotation.StringDef
import io.reactivex.Observable

interface MutableAdapter<T> {
	fun setItems(newItems: List<T>)
	fun addItem(newItem: T): Boolean
	fun updateItem(updatedItem: T): Boolean
	fun deleteItem(itemToDelete: T): Boolean

	fun setSortOrder(@SortOrder order: String)
	fun switchSortOrder()
	fun sort()

	fun onItemSelected(): Observable<T>

	@StringDef(SORT_NO, SORT_ASC, SORT_DESC)
	@Retention(AnnotationRetention.SOURCE)
	annotation class SortOrder

	companion object {
		const val SORT_NO = "no"
		const val SORT_ASC = "asc"
		const val SORT_DESC = "desc"
	}
}