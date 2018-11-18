package com.ancientlore.stickies.topicpicker

import android.content.Context
import android.databinding.Observable
import android.databinding.ObservableField
import com.ancientlore.stickies.BasicListViewModel
import com.ancientlore.stickies.data.model.Topic
import com.ancientlore.stickies.data.source.DataSource
import com.ancientlore.stickies.data.source.TopicsRepository

class TopicPickerViewModel(context: Context): BasicListViewModel<Topic>(context) {

	val searchField = ObservableField<String>("")

	private val searchText: String get() = searchField.get()!!

	private val topicsAdapter get() = adapter as TopicPickerListAdapter

	init {
		loadAllTopics()
		observeSearchField()
	}

	override fun createListAdapter(context: Context) = TopicPickerListAdapter(context)

	private fun loadAllTopics() {
		TopicsRepository.getAllTopics(object : DataSource.SimpleRequestCallback<List<Topic>>() {
			override fun onSuccess(result: List<Topic>) = topicsAdapter.setItems(result)
		})
	}

	private fun observeSearchField() {
		searchField.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
			override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
				topicsAdapter.filter.filter(searchText)
			}
		})
	}
}