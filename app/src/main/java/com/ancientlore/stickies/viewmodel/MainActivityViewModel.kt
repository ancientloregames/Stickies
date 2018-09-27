package com.ancientlore.stickies.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.ancientlore.stickies.MutableAdapter
import com.ancientlore.stickies.db.NotesDatabase
import com.ancientlore.stickies.model.Note
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivityViewModel(application: Application,
							private val listAdapter: MutableAdapter<Note>)
	: AndroidViewModel(application) {

	private val dbExec: ExecutorService = Executors.newSingleThreadExecutor { r -> Thread(r, "db_worker") }

	private val db = NotesDatabase.getInstance(application.baseContext).noteDao()

	init {
		loadNotes()
	}

	private fun loadNotes() {
		dbExec.submit {
			val notesList = db.getAll()
			listAdapter.setItems(notesList)
		}
	}
}