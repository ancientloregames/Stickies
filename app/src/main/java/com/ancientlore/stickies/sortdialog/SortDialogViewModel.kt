package com.ancientlore.stickies.sortdialog

import android.app.Application
import android.databinding.ObservableField
import com.ancientlore.stickies.BasicViewModel
import com.ancientlore.stickies.C
import com.ancientlore.stickies.R
import com.ancientlore.stickies.SortOrder
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class SortDialogViewModel(application: Application, currentOrder: String)
	: BasicViewModel(application) {

	val orderField = ObservableField<String>(getSortOrderString(currentOrder))

	private var currentOrder: String = currentOrder
		set(value) {
			field = value
			orderField.set(getSortOrderString(currentOrder))
		}

	private val onByTitle = PublishSubject.create<String>()
	private val onByDateCreation = PublishSubject.create<String>()

	fun onByTitleClicked() = onByTitle.onNext(currentOrder)

	fun onByDateCreationClicked() = onByDateCreation.onNext(currentOrder)

	fun onSwitchOrderClicked() { currentOrder = getReverseSortOrder(currentOrder) }

	fun observeByTitleClicked() = onByTitle as Observable<String>

	fun observeByDateCreationClicked() = onByDateCreation as Observable<String>

	private fun getReverseSortOrder(@SortOrder currentOrder: String) = when (currentOrder) {
		C.ORDER_ASC -> C.ORDER_DESC
		C.ORDER_DESC -> C.ORDER_ASC
		else -> throw RuntimeException("Error! Unknown sorting order $currentOrder")
	}

	private fun getSortOrderString(@SortOrder currentOrder: String) = when(currentOrder) {
		C.ORDER_ASC -> getApplication<Application>().getString(R.string.order_asc)
		C.ORDER_DESC -> getApplication<Application>().getString(R.string.order_desc)
		else -> throw RuntimeException("Error! Unknown sorting order $currentOrder")
	}
}