package com.ancientlore.stickies.data.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.text.TextUtils
import com.ancientlore.stickies.utils.toPlainText
import java.text.DateFormat
import java.util.*

@Entity(tableName = "notes")
data class Note(@PrimaryKey(autoGenerate = true) var id: Long = 0,
				@field:ColumnInfo var timeCreated: Long = System.currentTimeMillis(),
				@field:ColumnInfo var timeUpdated: Long = 0,
				@field:ColumnInfo var timeCompleted: Long = 0,
				@field:ColumnInfo var timeNotify: Long = 0,
				@field:ColumnInfo var title: String = "",
				@field:ColumnInfo var body: String = "",
				@field:ColumnInfo var color: Int = -0x45,
				@field:ColumnInfo var icon: String = "",
				@field:ColumnInfo var topic: String = "",
				@field:ColumnInfo var isImportant: Boolean = false,
				@field:ColumnInfo var isCompleted: Boolean = false) {

	companion object {
		fun newInstance(finalId: Long, note: Note): Note {
			return Note(finalId,
					note.timeCreated,
					note.timeUpdated,
					note.timeCreated,
					note.timeNotify,
					note.title,
					note.body,
					note.color,
					note.icon,
					note.topic,
					note.isImportant,
					note.isCompleted)
		}
	}

	@delegate:Ignore private val dateCreated by lazy { Date(timeCreated) }
	@delegate:Ignore private val dateUpdated by lazy { Date(timeUpdated) }
	@delegate:Ignore private val dateCompleted by lazy { Date(timeCompleted) }
	@delegate:Ignore private val dateNotify by lazy { Date(timeNotify) }
	@delegate:Ignore val plainBody by lazy { body.toPlainText() }

	override fun equals(other: Any?): Boolean {
		return other is Note
				&& other.id == id
				&& other.timeCreated == timeCreated
				&& other.timeUpdated == timeUpdated
				&& other.timeCompleted == timeCompleted
				&& other.timeNotify == timeNotify
				&& other.isImportant == isImportant
				&& other.isCompleted == isCompleted
				&& other.color == color
				&& TextUtils.equals(other.title, title)
				&& TextUtils.equals(other.body, body)
				&& TextUtils.equals(other.icon, icon)
				&& TextUtils.equals(other.topic, topic)
	}

	override fun hashCode(): Int {
		var result = id.hashCode()
		result = 31 * result + timeCreated.hashCode()
		result = 31 * result + timeUpdated.hashCode()
		result = 31 * result + timeCompleted.hashCode()
		result = 31 * result + timeNotify.hashCode()
		result = 31 * result + title.hashCode()
		result = 31 * result + body.hashCode()
		result = 31 * result + color.hashCode()
		result = 31 * result + icon.hashCode()
		result = 31 * result + topic.hashCode()
		result = 31 * result + isImportant.hashCode()
		result = 31 * result + isCompleted.hashCode()
		return result
	}

	fun compareByText(other: Note): Int {
		return when {
			title.isNotEmpty() && other.title.isNotEmpty() ->
				title.compareTo(other.title)
			title.isNotEmpty() && other.body.isNotEmpty() ->
				title.compareTo(other.body)
			body.isNotEmpty() && other.title.isNotEmpty() ->
				body.compareTo(other.title)
			body.isNotEmpty() && other.body.isNotEmpty() ->
				body.compareTo(other.body)
			else -> 0
		}
	}

	fun getDateCreated(dateStyle: Int) = DateFormat.getDateInstance(dateStyle).format(dateCreated)!!

	fun getDateUpdated(dateStyle: Int) = DateFormat.getDateInstance(dateStyle).format(dateUpdated)!!

	fun getDateCreated(dateStyle: Int, timeStyle: Int) = DateFormat.getDateTimeInstance(dateStyle, timeStyle).format(dateCreated)!!

	fun getDateUpdated(dateStyle: Int, timeStyle: Int) = DateFormat.getDateTimeInstance(dateStyle, timeStyle).format(dateUpdated)!!
}