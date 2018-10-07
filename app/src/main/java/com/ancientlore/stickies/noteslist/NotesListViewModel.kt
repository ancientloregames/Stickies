package com.ancientlore.stickies.noteslist

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.util.Log
import com.ancientlore.stickies.BasicViewModel
import com.ancientlore.stickies.EmptyObject
import com.ancientlore.stickies.MutableAdapter
import com.ancientlore.stickies.R
import com.ancientlore.stickies.addeditnote.AddEditNoteActivity
import com.ancientlore.stickies.data.model.Note
import com.ancientlore.stickies.data.source.DataSource
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class NotesListViewModel(application: Application,
						 private val listAdapter: MutableAdapter<Note>)
	: BasicViewModel(application) {

	companion object {
		private const val TAG = "MainActivityViewModel"

		const val INTENT_ADD_NOTE = 101
		const val INTENT_SHOW_NOTE = 102
	}

	private val addNoteEvent = PublishSubject.create<Any>()
	private val showNoteEvent = PublishSubject.create<Long>()
	private val onShowFilterMenu = PublishSubject.create<Any>()

	init {
		loadNotes()

		listAdapter.onItemSelected()
				.subscribe { showNoteEvent.onNext(it.id) }
	}

	override fun handleOptionSelection(optionId: Int): Boolean {
		when (optionId) {
			R.id.filter -> onShowFilterMenu.onNext(EmptyObject)
			R.id.sortDirection -> switchSortDirection()
		}

		return true
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
			loadNote(getLongExtra(AddEditNoteActivity.EXTRA_NOTE_ID, 0))
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

	private fun loadImportantNotes() {
		repository.getImportant(object : DataSource.ListLoadedCallback<Note> {
			override fun onSuccess(data: List<Note>) = setListItems(data)
			override fun onFailure(error: Throwable) {
				Log.w(TAG, error.message ?: "Some error occurred during the important notes loading")
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

	private fun switchSortDirection() {
		listAdapter.switchSortOrder()
		listAdapter.sort()
	}

	fun handleFilterSelected(filterId: Int) : Boolean {
		when (filterId) {
			R.id.all -> loadNotes()
			R.id.important -> loadImportantNotes()
		}

		return true
	}

	fun onAddNoteClicked() = addNoteEvent.onNext(EmptyObject)

	fun onAddNote() = addNoteEvent as Observable<*>

	fun onShowNote() = showNoteEvent as Observable<Long>

	fun onShowFilterMenu() = onShowFilterMenu as Observable<*>
}