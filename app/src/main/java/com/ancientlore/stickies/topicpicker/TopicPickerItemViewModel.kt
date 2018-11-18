package com.ancientlore.stickies.topicpicker

import com.ancientlore.stickies.BasicListItemViewModel
import com.ancientlore.stickies.data.model.Topic

class TopicPickerItemViewModel(item: Topic): BasicListItemViewModel<Topic>(item) {
	interface Listener: BasicListItemViewModel.Listener<Topic>
}