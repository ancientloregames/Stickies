package com.ancientlore.stickies

import android.app.Application
import android.content.Context
import android.support.annotation.CallSuper
import com.ancientlore.stickies.data.source.NotesRepository
import com.ancientlore.stickies.data.source.TopicsRepository
import com.ancientlore.stickies.data.source.local.NotesDatabase
import com.ancientlore.stickies.notice.AlarmReceiver
import com.ancientlore.stickies.utils.cancelReminder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


abstract class NotesViewModel(application: Application) : BasicViewModel(application) {

	protected val repository = NotesRepository
	protected val topicsRep = TopicsRepository

	init {
		initRepositories(application.baseContext)
	}

	protected fun getFirebaseUser() = FirebaseAuth.getInstance().currentUser

	protected fun initRemoteRepositories(user: FirebaseUser) {
		repository.initRemoteSource(user.uid)
		topicsRep.initRemoteSource(user.uid)
	}

	private fun initLocalRepositories(database: NotesDatabase) {
		repository.initLocalSource(database.notesDao())
		topicsRep.initLocalSource(database.topicsDao())
	}

	private fun initRepositories(context: Context) {
		initLocalRepositories(NotesDatabase.getInstance(context))
		getFirebaseUser()?.let {
			initRemoteRepositories(it)
		}
	}

	@CallSuper
	protected open fun deleteNote(id: Long) {
		context.cancelReminder(id.toInt(), AlarmReceiver::class.java)
		repository.deleteItem(id)
	}
}