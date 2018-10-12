package com.ancientlore.stickies.noteslist

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.util.Log
import com.ancientlore.stickies.*
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

		const val OPTION_FILTER = "option_filter"
		const val OPTION_SORT = "option_sort"
	}

	private var currentSortOrder = C.ORDER_ASC

	private val addNoteEvent = PublishSubject.create<Any>()
	private val showNoteEvent = PublishSubject.create<Long>()
	private val onShowFilterMenu = PublishSubject.create<Any>()
	private val onShowSortMenu = PublishSubject.create<String>()

	init {
		loadNotes()

		listAdapter.onItemSelected()
				.subscribe { showNoteEvent.onNext(it.id) }
	}

	override fun handleOptionSelection(option: String): Boolean {
		when (option) {
			OPTION_FILTER -> onShowFilterMenu.onNext(EmptyObject)
			OPTION_SORT -> onShowSortMenu.onNext(currentSortOrder)
			else -> return false
		}
		return true
	}

	override fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		when (requestCode) {
			INTENT_ADD_NOTE -> handleNoteAdditionResult(resultCode, data)
			INTENT_SHOW_NOTE -> handleShowNoteResult(resultCode, data)
		}
	}

	private fun handleNoteAdditionResult(resultCode: Int, data: Intent?) = when (resultCode) {
		Activity.RESULT_OK -> getIdAndLoadNote(data)
		else -> Unit
	}

	private fun handleShowNoteResult(resultCode: Int, data: Intent?) = when (resultCode) {
		Activity.RESULT_OK -> getIdAndDeleteNote(data)
		else -> Unit
	}

	private fun getIdAndLoadNote(data: Intent?) {
		data?.run {
			loadNote(getLongExtra(AddEditNoteActivity.EXTRA_NOTE_ID, 0))
		} ?: Log.w(TAG, "No note id in the NoteActivity data, finished with Success!")
	}

	private fun getIdAndDeleteNote(data: Intent?) {
		data?.run {
			deleteNote(getLongExtra(AddEditNoteActivity.EXTRA_NOTE_ID, 0))
		}
	}

	private fun loadNotes() {
		repository.getAll(object : DataSource.SimpleRequestCallback<List<Note>>()  {
			override fun onSuccess(result: List<Note>) = setListItems(result)
		})
	}

	private fun loadImportantNotes() {
		repository.getImportant(object : DataSource.SimpleRequestCallback<List<Note>>()  {
			override fun onSuccess(result: List<Note>) = setListItems(result)
		})
	}

	private fun loadNote(id: Long) {
		repository.getItem(id, object : DataSource.SimpleRequestCallback<Note>() {
			override fun onSuccess(result: Note) = addListItem(result)
		})
	}

	private fun deleteNote(id: Long) = deleteListItem(id)

	private fun setListItems(items: List<Note>) = runOnUiThread(Runnable { listAdapter.setItems(items) })

	private fun addListItem(item: Note) = runOnUiThread(Runnable { listAdapter.addItem(item) })

	private fun deleteListItem(id: Long) = runOnUiThread(Runnable { listAdapter.deleteItem(id) })

	fun handleFilterSelected(filterId: Int) : Boolean {
		when (filterId) {
			R.id.all -> loadNotes()
			R.id.important -> loadImportantNotes()
		}

		return true
	}

	fun sort(@SortField field: String, @SortOrder order: String) {
		currentSortOrder = order
		listAdapter.sort(field, order)
	}

	fun onAddNoteClicked() = addNoteEvent.onNext(EmptyObject)

	fun onAddNote() = addNoteEvent as Observable<*>

	fun onShowNote() = showNoteEvent as Observable<Long>

	fun onShowFilterMenu() = onShowFilterMenu as Observable<*>

	fun onShowSortMenu() = onShowSortMenu as Observable<String>
}