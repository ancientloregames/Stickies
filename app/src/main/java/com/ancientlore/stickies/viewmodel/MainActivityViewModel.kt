package com.ancientlore.stickies.viewmodel

import android.app.Application
import android.content.Intent
import android.util.Log
import com.ancientlore.stickies.MutableAdapter
import com.ancientlore.stickies.data.model.Note
import com.ancientlore.stickies.data.source.DataSource
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class MainActivityViewModel(application: Application,
							private val listAdapter: MutableAdapter<Note>)
	: BasicViewModel(application) {

	companion object {
		private const val TAG = "MainActivityViewModel"

		private const val INTENT_ADD_NOTE = 101
	}

	private val addNoteEvent = PublishSubject.create<Int>()

	init {
		loadNotes()
	}

	private fun loadNotes() {
		repository.getAll(object : DataSource.ListLoadedCallback<Note> {
			override fun onSuccess(data: List<Note>) {
				listAdapter.setItems(data)
			}

			override fun onFailure(error: Throwable) {
				Log.w(TAG, error.message ?: "Some error occurred during the list loading")
			}
		})
	}

	override fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
	}

	fun onAddNoteClicked() = addNoteEvent.onNext(INTENT_ADD_NOTE)

	fun onAddNote() = addNoteEvent as Observable<Int>
}