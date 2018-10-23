package com.ancientlore.stickies.noteslist

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.databinding.ObservableField
import android.util.Log
import com.ancientlore.stickies.*
import com.ancientlore.stickies.data.model.Note
import com.ancientlore.stickies.data.source.DataSource
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class NotesListViewModel(application: Application,
						 private val listAdapter: MutableAdapter<Note>)
	: NotesViewModel(application) {

	companion object {
		private const val TAG = "NotesListViewModel"

		const val INTENT_ADD_NOTE = 100
		const val INTENT_EDIT_NOTE = 101
		const val INTENT_SHOW_NOTE = 102

		const val OPTION_FILTER = 0
		const val OPTION_SORT = 1

		const val FILTER_ALL = 0
		const val FILTER_IMPORTANT = 1
	}

	val isEmpty = ObservableField<Boolean>(true)

	private var currentSortOrder = C.ORDER_ASC

	private val addNoteEvent = PublishSubject.create<Any>()
	private val editNoteEvent = PublishSubject.create<Long>()
	private val showNoteEvent = PublishSubject.create<Long>()
	private val showFilterMenuEvent = PublishSubject.create<Any>()
	private val showSortMenuEvent = PublishSubject.create<String>()
	private val requestScrollToTop = PublishSubject.create<Any>()

	init {
		loadAllNotes()

		listAdapter.onItemSelected()
				.subscribe { showNoteEvent.onNext(it.id) }
	}

	override fun handleOptionSelection(option: Int): Boolean {
		when (option) {
			OPTION_FILTER -> showFilterMenuEvent.onNext(EmptyObject)
			OPTION_SORT -> showSortMenuEvent.onNext(currentSortOrder)
			else -> return false
		}
		return true
	}

	override fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		when (requestCode) {
			INTENT_ADD_NOTE -> handleNoteAdditionResult(resultCode, data)
			INTENT_EDIT_NOTE -> handleNoteEditingResult(resultCode, data)
			INTENT_SHOW_NOTE -> handleNoteShowingResult(resultCode, data)
			else -> Log.w(TAG, "Unknown requestCode $requestCode")
		}
	}

	fun handleFilterSelection(filter: Int) {
		when (filter) {
			FILTER_ALL -> loadAllNotes()
			FILTER_IMPORTANT -> loadImportantNotes()
			else -> Log.w(TAG, "Unknown filter $filter")
		}
	}

	fun sort(@SortField field: String, @SortOrder order: String) {
		currentSortOrder = order
		listAdapter.sort(field, order)
	}

	fun onAddNoteClicked() = addNoteEvent.onNext(EmptyObject)

	fun observeAddNote() = addNoteEvent as Observable<*>

	fun observeEditNote() = editNoteEvent as Observable<Long>

	fun observeShowNote() = showNoteEvent as Observable<Long>

	fun observeShowFilterMenu() = showFilterMenuEvent as Observable<*>

	fun observeShowSortMenu() = showSortMenuEvent as Observable<String>

	fun observeScrollToTopRequest() = requestScrollToTop as Observable<*>

	private fun handleNoteAdditionResult(resultCode: Int, data: Intent?) {
		when (resultCode) {
			Activity.RESULT_OK -> getNoteId(data)?.let {
				loadAndAddNote(it)
			} ?: Log.w(TAG, "No note id in the NoteActivity data, finished with Success!")
			else -> Log.w(TAG, "Note addition intent finished with resultCode $resultCode")
		}
	}

	private fun handleNoteEditingResult(resultCode: Int, data: Intent?) {
		when (resultCode) {
			Activity.RESULT_OK -> getNoteId(data)?.let {
				loadAndUpdateNote(it)
			} ?: Log.w(TAG, "No note id in the NoteActivity data, finished with Success!")
			else -> Log.w(TAG, "Note editing intent finished with resultCode $resultCode")
		}
	}

	private fun handleNoteShowingResult(resultCode: Int, data: Intent?) {
		when (resultCode) {
			Activity.RESULT_OK -> data?.action?.let { handleNoteShowingResult(it, data) }
			else -> Log.w(TAG, "Note showing intent finished with resultCode $resultCode")
		}
	}

	private fun handleNoteShowingResult(action: String, data: Intent?) {
		when (action) {
			C.ACTION_EDIT -> getNoteId(data)?.let { editNoteEvent.onNext(it) }
			C.ACTION_DELETE -> getNoteId(data)?.let { deleteNote(it) }
			else -> Log.w(TAG, "Note showing intent finished with unknown action $action")
		}
	}

	private fun loadAllNotes() {
		repository.getAll(object : DataSource.SimpleRequestCallback<List<Note>>()  {
			override fun onSuccess(result: List<Note>) = setListItems(result)
		})
	}

	private fun loadImportantNotes() {
		repository.getImportant(object : DataSource.SimpleRequestCallback<List<Note>>()  {
			override fun onSuccess(result: List<Note>) = setListItems(result)
		})
	}

	private fun loadAndAddNote(id: Long) {
		repository.getItem(id, object : DataSource.SimpleRequestCallback<Note>() {
			override fun onSuccess(result: Note) = addListItem(result)
		})
	}

	private fun loadAndUpdateNote(id: Long) {
		repository.getItem(id, object : DataSource.SimpleRequestCallback<Note>() {
			override fun onSuccess(result: Note) = updateListItem(result)
		})
	}

	private fun deleteNote(id: Long) = deleteListItem(id)

	private fun setListItems(items: List<Note>) {
		isEmpty.set(items.isEmpty())
		runOnUiThread(Runnable {
			listAdapter.setItems(items)
			requestScrollToTop.onNext(EmptyObject)
		})
	}

	private fun addListItem(item: Note) {
		isEmpty.set(false)
		runOnUiThread(Runnable {
			listAdapter.prependItem(item)
			requestScrollToTop.onNext(EmptyObject)
		})
	}

	private fun updateListItem(item: Note) = runOnUiThread(Runnable { listAdapter.updateItem(item) })

	private fun deleteListItem(id: Long) {
		runOnUiThread(Runnable {
			listAdapter.deleteItem(id)
			isEmpty.set(listAdapter.isEmpty())
		})
	}

	private fun getNoteId(data: Intent?) = data?.extras?.run { getLong(C.EXTRA_NOTE_ID, C.DUMMY_ID) }?.takeIf { it != C.DUMMY_ID }
}