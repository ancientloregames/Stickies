package com.ancientlore.stickies.data.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.text.TextUtils
import java.text.DateFormat
import java.util.*

@Entity(tableName = "notes")
data class Note(@PrimaryKey(autoGenerate = true) val id: Long = 0,
				@field:ColumnInfo val timeCreated: Long = 0,
				@field:ColumnInfo val timeUpdated: Long = 0,
				@field:ColumnInfo val timeNotify: Long = 0,
				@field:ColumnInfo val title: String,
				@field:ColumnInfo val body: String = "",
				@field:ColumnInfo val color: String = "",
				@field:ColumnInfo val icon: String = "",
				@field:ColumnInfo val topic: String = "",
				@field:ColumnInfo val isImportant: Boolean = false,
				@field:ColumnInfo val isCompleted: Boolean = false) {

	@delegate:Ignore private val dateCreated by lazy { Date(timeCreated) }
	@delegate:Ignore private val dateUpdated by lazy { Date(timeUpdated) }
	@delegate:Ignore private val dateNotify by lazy { Date(timeNotify) }

	override fun equals(other: Any?): Boolean {
		return other is Note
				&& other.id == id
				&& other.timeCreated == timeCreated
				&& other.timeUpdated == timeUpdated
				&& other.timeNotify == timeNotify
				&& other.isImportant == isImportant
				&& other.isCompleted == isCompleted
				&& TextUtils.equals(other.title, title)
				&& TextUtils.equals(other.body, body)
				&& TextUtils.equals(other.color, color)
				&& TextUtils.equals(other.icon, icon)
				&& TextUtils.equals(other.topic, topic)
	}

	override fun hashCode(): Int {
		var result = id.hashCode()
		result = 31 * result + timeCreated.hashCode()
		result = 31 * result + timeUpdated.hashCode()
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

	fun getDateCreated(dateStyle: Int) = DateFormat.getDateInstance(dateStyle).format(dateCreated)!!

	fun getDateCreated(dateStyle: Int, timeStyle: Int) = DateFormat.getDateTimeInstance(dateStyle, timeStyle).format(dateCreated)!!
}