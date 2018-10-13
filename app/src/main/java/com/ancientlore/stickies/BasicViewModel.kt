package com.ancientlore.stickies

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.content.Context
import android.content.Intent
import android.os.Handler
import com.ancientlore.stickies.data.source.NotesRepository
import com.ancientlore.stickies.data.source.local.NotesDatabase

abstract class BasicViewModel(application: Application): AndroidViewModel(application) {

	private val uiHandler = Handler(application.mainLooper)

	protected val repository = NotesRepository

	init { initRepository(application.baseContext) }

	open fun handleOptionSelection(option: Int) = false

	open fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { }

	protected fun runOnUiThread(action: Runnable) { uiHandler.post(action) }

	private fun initRepository(context: Context) {
		val db = NotesDatabase.getInstance(context)
		repository.initLocalSource(db.notesDao())
	}
}