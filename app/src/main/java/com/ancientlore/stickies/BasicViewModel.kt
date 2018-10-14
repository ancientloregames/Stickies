package com.ancientlore.stickies

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.content.Intent
import android.os.Handler

abstract class BasicViewModel(application: Application): AndroidViewModel(application) {

	private val uiHandler = Handler(application.mainLooper)

	open fun handleOptionSelection(option: Int) = false

	open fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { }

	protected fun runOnUiThread(action: Runnable) { uiHandler.post(action) }
}