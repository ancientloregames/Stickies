package com.ancientlore.stickies.topicpicker

import android.content.Context
import android.view.View
import com.ancientlore.stickies.BasicUiFragment
import com.ancientlore.stickies.R
import com.ancientlore.stickies.data.model.Topic
import com.ancientlore.stickies.databinding.TopicpickerUiBinding

class TopicPickerFragment: BasicUiFragment<TopicPickerViewModel>() {

	interface Listener {
		fun onTopicSelected(topic: Topic)
	}

	private var listener: Listener? = null

	override fun getLayoutResId() = R.layout.topicpicker_ui

	override fun createViewModel(context: Context) = TopicPickerViewModel(context)

	override fun initViewModel(viewModel: TopicPickerViewModel) {
		viewModel.observeItemClicked()
				.take(1)
				.subscribe { listener?.onTopicSelected(it) }
	}

	override fun bind(view: View, viewModel: TopicPickerViewModel) {
		val binding = TopicpickerUiBinding.bind(view)
		binding.viewModel = viewModel
	}

	fun setListener(listener: Listener) { this.listener = listener }
}