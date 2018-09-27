package com.ancientlore.stickies.viewmodel

import android.app.Application
import android.content.Intent

class NoteActivityViewModel(application: Application): BasicViewModel(application) {

	override fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
	}
}