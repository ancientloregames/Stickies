package com.ancientlore.stickies.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.util.Log
import com.ancientlore.stickies.EmptyObject
import com.ancientlore.stickies.MutableAdapter
import com.ancientlore.stickies.data.model.Note
import com.ancientlore.stickies.data.source.DataSource
import com.ancientlore.stickies.ui.NoteActivity
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class MainActivityViewModel(application: Application,
							private val listAdapter: MutableAdapter<Note>)
	: BasicViewModel(application) {

	companion object {
		private const val TAG = "MainActivityViewModel"

		const val INTENT_ADD_NOTE = 101
		const val INTENT_SHOW_NOTE = 102
	}

	private val addNoteEvent = PublishSubject.create<Any>()

	private val showNoteEvent = PublishSubject.create<Long>()

	init {
		loadNotes()

		listAdapter.onItemSelected()
				.subscribe { showNoteEvent.onNext(it.id) }
	}

	override fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		when (requestCode) {
			INTENT_ADD_NOTE -> handleNoteAdditionResult(resultCode, data)
		}
	}

	private fun handleNoteAdditionResult(resultCode: Int, data: Intent?) {
		when (resultCode) {
			Activity.RESULT_OK -> getIdAndLoadNote(data)
		}
	}

	private fun getIdAndLoadNote(data: Intent?) {
		data?.run {
			loadNote(getLongExtra(NoteActivity.EXTRA_NOTE_ID, 0))
		} ?: Log.w(TAG, "No note id in the NoteActivity data, finished with Success!")
	}

	private fun loadNotes() {
		repository.getAll(object : DataSource.ListLoadedCallback<Note> {
			override fun onSuccess(data: List<Note>) = setListItems(data)
			override fun onFailure(error: Throwable) {
				Log.w(TAG, error.message ?: "Some error occurred during the list loading")
			}
		})
	}

	private fun loadNote(id: Long) {
		repository.getItem(id, object : DataSource.ItemLoadedCallback<Note> {
			override fun onSuccess(data: Note) = addListItem(data)
			override fun onFailure(error: Throwable) {
				Log.w(TAG, error.message ?: "Some error occurred during the loading of an item with id $id")
			}
		})
	}

	private fun setListItems(items: List<Note>) = runOnUiThread(Runnable { listAdapter.setItems(items) })

	private fun addListItem(item: Note) = runOnUiThread(Runnable { listAdapter.addItem(item) })

	fun onAddNoteClicked() = addNoteEvent.onNext(EmptyObject)

	fun onAddNote() = addNoteEvent as Observable<*>

	fun onShowNote() = showNoteEvent as Observable<Long>
}