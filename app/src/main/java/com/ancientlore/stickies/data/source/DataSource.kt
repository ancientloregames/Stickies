package com.ancientlore.stickies.data.source

import android.util.Log

interface DataSource<DataModel> {

	interface RequestCallback<DataModel> {
		fun onSuccess(result: DataModel)
		fun onFailure(error: Throwable)
	}

	abstract class SimpleRequestCallback<DataModel>: RequestCallback<DataModel> {
		override fun onFailure(error: Throwable) { Log.w("DataSource", error.message ?: "Some error accured during the request") }
	}

	fun getAll(callback: RequestCallback<List<DataModel>>)

	fun getItem(id: Long, callback: RequestCallback<DataModel>)

	fun insertItem(item: DataModel, callback: RequestCallback<Long>? = null)

	fun updateItem(item: DataModel)

	fun reset(newItems: List<DataModel>)

	fun deleteAll()

	fun deleteItem(id: Long)
}