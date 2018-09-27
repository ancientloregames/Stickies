package com.ancientlore.stickies.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.content.Intent

abstract class BasicViewModel(application: Application): AndroidViewModel(application) {

	abstract fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}