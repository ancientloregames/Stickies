package com.ancientlore.stickies

import android.app.Application
import android.content.Context
import android.support.annotation.CallSuper
import com.ancientlore.stickies.data.source.NotesRepository
import com.ancientlore.stickies.data.source.TopicsRepository
import com.ancientlore.stickies.data.source.local.NotesDatabase
import com.ancientlore.stickies.notice.AlarmReceiver
import com.ancientlore.stickies.utils.cancelReminder


abstract class NotesViewModel(application: Application) : BasicViewModel(application) {

	protected val repository = NotesRepository
	protected val topicsRep = TopicsRepository

	init {
		initRepository(application.baseContext)
	}

	private fun initRepository(context: Context) {
		val db = NotesDatabase.getInstance(context)
		repository.initLocalSource(db.notesDao())
		topicsRep.initLocalSource(db.topicsDao())
	}

	@CallSuper
	protected open fun deleteNote(id: Long) {
		context.cancelReminder(id.toInt(), AlarmReceiver::class.java)
		repository.deleteItem(id)
	}
}