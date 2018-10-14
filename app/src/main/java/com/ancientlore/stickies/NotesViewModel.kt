package com.ancientlore.stickies

import android.app.Application
import android.content.Context
import com.ancientlore.stickies.data.source.NotesRepository
import com.ancientlore.stickies.data.source.local.NotesDatabase


abstract class NotesViewModel(application: Application): BasicViewModel(application) {

  protected val repository = NotesRepository

  init { initRepository(application.baseContext) }

  private fun initRepository(context: Context) {
    val db = NotesDatabase.getInstance(context)
    repository.initLocalSource(db.notesDao())
  }
}