package com.ancientlore.stickies.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.content.Context
import android.content.Intent
import com.ancientlore.stickies.data.source.NotesRepository
import com.ancientlore.stickies.data.source.local.NotesDatabase

abstract class BasicViewModel(application: Application): AndroidViewModel(application) {

	protected val repository = NotesRepository

	init {
		initRepository(application.baseContext)
	}

	abstract fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

	private fun initRepository(context: Context) {
		val db = NotesDatabase.getInstance(context)
		repository.initLocalSource(db.notesDao())
	}
}