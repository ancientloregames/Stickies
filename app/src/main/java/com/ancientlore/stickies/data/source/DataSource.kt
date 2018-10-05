package com.ancientlore.stickies.data.source

interface DataSource<DataModel> {

	interface ListLoadedCallback<DataModel> {
		fun onSuccess(data: List<DataModel>)
		fun onFailure(error: Throwable)
	}

	interface ItemLoadedCallback<DataModel> {
		fun onSuccess(data: DataModel)
		fun onFailure(error: Throwable)
	}

	interface ItemInsertedCallback {
		fun onSuccess(id: Long)
		fun onFailure(error: Throwable)
	}

	fun getAll(callback: ListLoadedCallback<DataModel>)

	fun getImportant(callback: ListLoadedCallback<DataModel>)

	fun getItem(id: Long, callback: ItemLoadedCallback<DataModel>)

	fun insertItem(item: DataModel, callback: ItemInsertedCallback)

	fun deleteAll()

	fun deleteItem(id: Long)
}