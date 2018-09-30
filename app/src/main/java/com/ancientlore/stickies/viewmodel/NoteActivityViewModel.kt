package com.ancientlore.stickies.viewmodel

import android.app.Application
import android.databinding.ObservableField
import android.util.Log
import com.ancientlore.stickies.data.model.Note
import com.ancientlore.stickies.data.source.DataSource
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class NoteActivityViewModel(application: Application): BasicViewModel(application) {

	companion object {
		private const val TAG = "NoteActivityViewModel"
	}

	val titleField = ObservableField<String>("")
	val messageField = ObservableField<String>("")

	private val isValid get() = titleField.get()?.isNotEmpty() ?: false

	private val note get() = Note(0, titleField.get()!!, messageField.get()!!)

	private val onNoteAdded = PublishSubject.create<Long>()

	private fun addNote() {
		repository.insertItem(note, object : DataSource.ItemInsertedCallback {
			override fun onSuccess(id: Long) {
				onNoteAdded.onNext(id)
			}

			override fun onFailure(error: Throwable) {
				Log.w(TAG, error.message ?: "Some error occurred during the insertion of the note to Db")
			}
		})
	}

	fun onSubmitClicked() {
		if (isValid) {
			addNote()
		}
	}

	fun onNoteAdded() = onNoteAdded as Observable<Long>
}