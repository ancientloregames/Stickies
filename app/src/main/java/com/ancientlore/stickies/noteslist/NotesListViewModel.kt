package com.ancientlore.stickies.noteslist

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.net.Uri
import android.util.Log
import com.ancientlore.stickies.*
import com.ancientlore.stickies.data.model.Note
import com.ancientlore.stickies.data.model.Topic
import com.ancientlore.stickies.data.source.DataSource
import com.ancientlore.stickies.utils.marshall
import com.ancientlore.stickies.utils.split
import com.ancientlore.stickies.utils.unmarshall
import com.google.firebase.auth.FirebaseUser
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.IOException

class NotesListViewModel(application: Application,
						 private val listAdapter: NotesListAdapter)
	: NotesViewModel(application) {

	companion object {
		private const val TAG = "NotesListViewModel"

		const val INTENT_ADD_NOTE = 100
		const val INTENT_EDIT_NOTE = 101
		const val INTENT_SHOW_NOTE = 102
		const val INTENT_SHOW_TOPIC_PICKER = 103
		const val INTENT_EXPORT_NOTES = 104
		const val INTENT_IMPORT_NOTES = 105
		const val INTENT_CLOUD_AUTH = 106

		const val OPTION_FILTER = 0
		const val OPTION_SORT = 1
		const val OPTION_EXPORT = 2
		const val OPTION_IMPORT = 3
		const val OPTION_CLOUD = 4

		const val FILTER_ALL = 0
		const val FILTER_IMPORTANT = 1
		const val FILTER_TOPIC = 2
	}

	val isEmpty = ObservableField<Boolean>(true)
	val isQuickNoteMode = ObservableBoolean()

	private var currentSortOrder = C.ORDER_ASC
	private var currentTopic = ""

	private val onOpenNoteFormRequest = PublishSubject.create<Any>()
	private val editNoteEvent = PublishSubject.create<Long>()
	private val showNoteEvent = PublishSubject.create<Long>()
	private val showFilterMenuEvent = PublishSubject.create<Any>()
	private val showSortMenuEvent = PublishSubject.create<String>()
	private val requestScrollToTop = PublishSubject.create<Any>()
	private val showTopicPickerEvent = PublishSubject.create<String>()
	private val exportNotesEvent = PublishSubject.create<Any>()
	private val importNotesEvent = PublishSubject.create<Any>()
	private val cloudAuthRequest = PublishSubject.create<Any>()

	init {
		loadAllNotes()

		listAdapter.setListener(object : NotesListAdapter.Listener {
			override fun onItemClicked(note: Note) = showNoteEvent.onNext(note.id)
			override fun onNewNote(note: Note) = insertAndAddNote(note)
			override fun onImportantButtonClicked(id: Long, newState: Boolean) = switchImportance(id, newState)
			override fun onCompletedButtonClicked(id: Long, newState: Boolean) = switchComptetion(id, newState)
			override fun onDeleteButtonClicked(id: Long) = deleteNote(id)
		})
	}

	override fun handleOptionSelection(option: Int): Boolean {
		when (option) {
			OPTION_FILTER -> showFilterMenuEvent.onNext(EmptyObject)
			OPTION_SORT -> showSortMenuEvent.onNext(currentSortOrder)
			OPTION_EXPORT -> exportNotesEvent.onNext(EmptyObject)
			OPTION_IMPORT -> importNotesEvent.onNext(EmptyObject)
			OPTION_CLOUD -> syncOrAuth()
			else -> return false
		}
		return true
	}

	override fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		when (requestCode) {
			INTENT_ADD_NOTE -> handleNoteAdditionResult(resultCode, data)
			INTENT_EDIT_NOTE -> handleNoteEditingResult(resultCode, data)
			INTENT_SHOW_NOTE -> handleNoteShowingResult(resultCode, data)
			INTENT_SHOW_TOPIC_PICKER -> handleTopicPickerResult(resultCode, data)
			INTENT_EXPORT_NOTES -> handleExportNotesResult(resultCode, data)
			INTENT_IMPORT_NOTES -> handleImportNotesResult(resultCode, data)
			INTENT_CLOUD_AUTH -> handleCloudAuthResult(resultCode, data)
			else -> Log.w(TAG, "Unknown requestCode $requestCode")
		}
	}

	fun handleFilterSelection(filter: Int) {
		when (filter) {
			FILTER_ALL -> loadAllNotes()
			FILTER_IMPORTANT -> loadImportantNotes()
			FILTER_TOPIC -> showTopicPickerEvent.onNext(currentTopic)
			else -> Log.w(TAG, "Unknown filter $filter")
		}
	}

	fun sort(@SortField field: String, @SortOrder order: String) {
		currentSortOrder = order
		listAdapter.sort(field, order)
	}

	fun filterNotesList(constraint: String) = listAdapter.filter(constraint)

	fun onKeyboardStateChanged(opened: Boolean) = isQuickNoteMode.set(opened)

	fun onOpenNoteFormClicked() = onOpenNoteFormRequest.onNext(EmptyObject)

	fun onAddQuickNoteClicked() {
		when (isQuickNoteMode.get()) {
			true -> listAdapter.submitCurrentText()
			false -> requestQuickNote()
		}
	}

	fun observeOpenNoteFormRequest() = onOpenNoteFormRequest as Observable<*>

	fun observeEditNote() = editNoteEvent as Observable<Long>

	fun observeShowNote() = showNoteEvent as Observable<Long>

	fun observeShowFilterMenu() = showFilterMenuEvent as Observable<*>

	fun observeShowSortMenu() = showSortMenuEvent as Observable<String>

	fun observeScrollToTopRequest() = requestScrollToTop as Observable<*>

	fun observeShowTopicPickerRequest() = showTopicPickerEvent as Observable<String>

	fun observeExportNotesRequest() = exportNotesEvent as Observable<*>

	fun observeImportNotesRequest() = importNotesEvent as Observable<*>

	fun observeCloudAuthRequest() = cloudAuthRequest as Observable<*>

	private fun requestQuickNote() {
		requestScrollToTop.onNext(EmptyObject)
		listAdapter.requestNoteAddition()
	}

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

	private fun handleTopicPickerResult(resultCode: Int, data: Intent?) {
		when (resultCode) {
			Activity.RESULT_OK -> data?.getParcelableExtra<Topic>(C.EXTRA_TOPIC)?.let {
				loadTopicNotes(it)
			}
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

	private fun handleExportNotesResult(resultCode: Int, data: Intent?) {
		when (resultCode) {
			Activity.RESULT_OK -> data?.data?.let { exportNotes(it) }
			else -> Log.w(TAG, "Exporting intent finished with resultCode $resultCode")
		}
	}

	private fun handleImportNotesResult(resultCode: Int, data: Intent?) {
		when (resultCode) {
			Activity.RESULT_OK -> data?.data?.let { importNotes(it) }
			else -> Log.w(TAG, "Importing intent finished with resultCode $resultCode")
		}
	}

	private fun handleCloudAuthResult(resultCode: Int, data: Intent?) {
		when (resultCode) {
			Activity.RESULT_OK -> if (!tryCloudSync())
				Log.wtf(TAG, "Firebase auth finished with success, but no user was found")
			else -> Log.w(TAG, "Importing intent finished with resultCode $resultCode")
		}
	}

	private fun exportNotes(uri: Uri) {
		try {
			val stream = context.contentResolver.openOutputStream(uri)
			val writer = BufferedOutputStream(stream)
			listAdapter.itemsSequence.forEach {
				writer.write(it.marshall())
				writer.write("\n\r".toByteArray())
			}
			writer.flush()
			writer.close()
		} catch (e: IOException) {
			e.printStackTrace()
		}
	}

	private fun importNotes(uri: Uri) {
		try {
			val reader = BufferedInputStream(context.contentResolver.openInputStream(uri))
			val bytes = reader.readBytes()
			reader.close()
			val list = bytes.split("\n\r".toByteArray())
			val notes = list.unmarshall(Note.CREATOR)
			resetNotes(notes)
		} catch (e: IOException) {
			e.printStackTrace()
		}
	}

	private fun syncOrAuth() {
		val isAuthRequered = !tryCloudSync()
		if (isAuthRequered) cloudAuthRequest.onNext(EmptyObject)
	}

	private fun tryCloudSync() = getFirebaseUser()?.let { onCloudAuthSuccess(it);true } ?: false

	private fun onCloudAuthSuccess(user: FirebaseUser) {
		initRemoteRepositories(user)
		repository.getAllRemotely(object : DataSource.SimpleRequestCallback<List<Note>>()  {
			override fun onSuccess(result: List<Note>) = setListItems(result)
		})

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

	private fun loadTopicNotes(topic: Topic) {
		repository.getAllByTopic(topic, object : DataSource.SimpleRequestCallback<List<Note>>()  {
			override fun onSuccess(result: List<Note>) = setListItems(result)
		})
	}

	private fun insertAndAddNote(note: Note) {
		repository.insertItem(note, object : DataSource.SimpleRequestCallback<Long>() {
			override fun onSuccess(result: Long) = addListItem(Note.newInstance(result, note))
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

	private fun resetNotes(newNotes: List<Note>) {
		repository.reset(newNotes)
		listAdapter.setItems(newNotes)
	}

	private fun switchImportance(id: Long, isImportant: Boolean) {
		repository.switchImportance(id, isImportant)
		runOnUiThread(Runnable {
			listAdapter.updateImportance(id, isImportant)
		})
	}

	private fun switchComptetion(id: Long, isCompleted: Boolean) {
		repository.switchCompletion(id, isCompleted)
		runOnUiThread(Runnable {
			listAdapter.updateCompletion(id, isCompleted)
		})
	}

	override fun deleteNote(id: Long) {
		super.deleteNote(id)
		deleteListItem(id)
	}

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