package com.ancientlore.stickies.data.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.text.TextUtils

@Entity(tableName = "notes")
data class Note(@PrimaryKey(autoGenerate = true) val id: Long,
				@field:ColumnInfo val timestamp: Long,
				@field:ColumnInfo val title: String,
				@field:ColumnInfo val body: String,
				@field:ColumnInfo val isImportant: Boolean) {

	override fun equals(other: Any?): Boolean {
		return other is Note
				&& other.id == id
				&& other.timestamp == timestamp
				&& other.isImportant == isImportant
				&& TextUtils.equals(other.title, title)
				&& TextUtils.equals(other.body, body)
	}

	override fun hashCode(): Int {
		var result = id.hashCode()
		result = 31 * result + timestamp.hashCode()
		result = 31 * result + title.hashCode()
		result = 31 * result + body.hashCode()
		result = 31 * result + isImportant.hashCode()
		return result
	}
}