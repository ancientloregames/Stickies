package com.ancientlore.stickies.addeditnote

import android.app.Application
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Html
import android.text.SpannableString
import com.ancientlore.stickies.EmptyObject
import com.ancientlore.stickies.NotesViewModel
import com.ancientlore.stickies.R
import com.ancientlore.stickies.data.model.Note
import com.ancientlore.stickies.data.model.Topic
import com.ancientlore.stickies.data.source.DataSource
import com.ancientlore.stickies.menu.topic.TopicsListAdapter
import com.ancientlore.stickies.utils.spannedBody
import com.ancientlore.stickies.utils.tryScheduleAlarm
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.*

class AddEditNoteViewModel(application: Application): NotesViewModel(application) {

	companion object {
		const val OPTION_IMPORTANT = 0
		const val OPTION_COMPLETED = 1
		const val OPTION_PICKCOLOR = 2
		const val OPTION_SCHEDULEALARM = 3

		private const val NOTE_VALID = 0
		const val ALERT_EMPTY = 1
		const val ALERT_TITLE_LONG = 2
		const val ALERT_BODY_LONG = 3

		const val TITLE_LIMIT = 256
		const val BODY_LIMIT = 2048

		private const val STATE_TITLE = "state_title"
		private const val STATE_BODY = "state_body"
		private const val STATE_TOPIC = "state_topic"
		private const val STATE_COLOR = "state_color"
		private const val STATE_IMPORTANCE = "state_importance"
		private const val STATE_COMPLETED = "state_completed"
	}

	data class State(val isImportant: Boolean, val isCompleted: Boolean)

	val titleField = ObservableField<String>("")
	val messageField = ObservableField<CharSequence>(SpannableString(""))
	val topicField = ObservableField<String>("")
	val colorField = ObservableInt(ContextCompat.getColor(context, R.color.noteYellow))
	val topicsAdapter = TopicsListAdapter(context)

	private var editedNote: Note? = null

	private val noteTitle get() = titleField.get()!!
	private val noteBody get() = Html.toHtml(SpannableString.valueOf(messageField.get()!!))
	private val noteColor get() = colorField.get()
	private val noteTopic get() = topicField.get()!!
	private var isImportant = false
	private var isCompleted = false
	private var timeNotify = 0L

	private val onNoteAdded = PublishSubject.create<Long>()
	private val onMenuCalled = PublishSubject.create<State>()
	private val onColorPickerCalled = PublishSubject.create<Int>()
	private val onTimePickerCalled = PublishSubject.create<Any>()
	private val onAlert = PublishSubject.create<Int>()

	init { loadTopics() }

	constructor(application: Application, noteId: Long) : this(application) {
		loadNote(noteId)
	}

	override fun saveState(bundle: Bundle) {
		bundle.putInt(STATE_COLOR, colorField.get())
		bundle.putString(STATE_TITLE, titleField.get())
		bundle.putCharSequence(STATE_BODY, messageField.get())
		bundle.putString(STATE_TOPIC, topicField.get())
		bundle.putBoolean(STATE_IMPORTANCE, isImportant)
		bundle.putBoolean(STATE_COMPLETED, isCompleted)
	}

	override fun loadState(bundle: Bundle) {
		colorField.set(bundle.getInt(STATE_COLOR))
		titleField.set(bundle.getString(STATE_TITLE))
		messageField.set(bundle.getCharSequence(STATE_BODY))
		topicField.set(bundle.getString(STATE_TOPIC))
		isImportant = bundle.getBoolean(STATE_IMPORTANCE)
		isCompleted = bundle.getBoolean(STATE_COMPLETED)
	}

	override fun handleOptionSelection(option: Int): Boolean {
		when (option) {
			OPTION_IMPORTANT -> switchImportance()
			OPTION_COMPLETED -> switchCompletion()
			OPTION_PICKCOLOR -> onColorPickerCalled.onNext(colorField.get())
			OPTION_SCHEDULEALARM -> onTimePickerCalled.onNext(EmptyObject)
			else -> return false
		}
		return true
	}

	fun setColor(color: Int) = colorField.set(color)

	fun setReminderTime(date: Date) { timeNotify = date.time }

	fun onSubmitClicked() {
		if (isValid())
			addNote()
	}

	fun onMenuButtonClicked() = onMenuCalled.onNext(State(isImportant, isCompleted))

	fun observeNoteAdded() = onNoteAdded as Observable<Long>

	fun observeMenuCalled() = onMenuCalled as Observable<State>

	fun observeColorPickerCalled() = onColorPickerCalled as Observable<Int>

	fun observeTimePickerCalled() = onTimePickerCalled as Observable<Any>

	fun observeAlert() = onAlert as Observable<Int>

	private fun loadTopics() {
		topicsRep.getAllTopics(object : DataSource.SimpleRequestCallback<List<Topic>>() {
			override fun onSuccess(result: List<Topic>) = topicsAdapter.setItems(result)
		})
	}

	private fun loadNote(id: Long) {
		repository.getItem(id, object : DataSource.SimpleRequestCallback<Note>() {
			override fun onSuccess(result: Note) = bind(result)
		})
	}

	private fun bind(note: Note) {
		editedNote = note
		colorField.set(note.color)
		titleField.set(note.title)
		topicField.set(note.topic)
		messageField.set(note.spannedBody())
		isImportant = note.isImportant
		isCompleted = note.isImportant
	}

	private fun addNote() {
		val note = composeNote()
		repository.insertItem(note, object : DataSource.SimpleRequestCallback<Long>() {
			override fun onSuccess(result: Long) = onNoteIdAssigned(result, note)
		})
	}

	private fun onNoteIdAssigned(noteId: Long, note: Note) {
		note.id = noteId
		note.tryScheduleAlarm(context)
		addTopic(note.topic)
		onNoteAdded.onNext(noteId)
	}

	private fun addTopic(topicName: String) {
		if (topicName.isNotEmpty())
			topicsRep.insertTopic(Topic(name = topicName))
	}

	private fun switchImportance() { isImportant = !isImportant }

	private fun switchCompletion() { isCompleted = !isCompleted }

	private fun composeNote() = editedNote?.let { composeNoteBasedOn(it) } ?: composeNewNote()

	private fun composeNewNote(): Note {
		return Note(
				timeCreated = System.currentTimeMillis(),
				timeNotify = timeNotify,
				title = noteTitle,
				body = noteBody,
				color = noteColor,
				topic = noteTopic,
				isImportant = isImportant,
				isCompleted = isCompleted)
	}

	private fun composeNoteBasedOn(note: Note): Note {
		return Note(id = note.id,
				timeCreated = note.timeCreated,
				timeUpdated = System.currentTimeMillis(),
				timeNotify = note.timeNotify,
				title = noteTitle,
				body = noteBody,
				color = noteColor,
				icon = note.icon,
				topic = noteTopic,
				isImportant = isImportant,
				isCompleted = isCompleted)
	}

	private fun isValid(): Boolean {
		val messageId = getValidityMessageId()

		return if (messageId != NOTE_VALID) {
			onAlert.onNext(messageId)
			false
		} else true
	}

	private fun getValidityMessageId(): Int {
		return when {
			noteTitle.isEmpty() && noteBody.isEmpty() -> ALERT_EMPTY
			noteTitle.length > TITLE_LIMIT -> ALERT_TITLE_LONG
			noteBody.length > BODY_LIMIT -> ALERT_BODY_LONG
			else -> NOTE_VALID
		}
	}
}