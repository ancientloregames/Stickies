package com.ancientlore.stickies.data.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "topics")
data class Topic(@field:PrimaryKey(autoGenerate = true) var id: Long = 0,
				 @field:ColumnInfo var title: String = "") {

	companion object {
		fun newInstance(newId: Long, original: Topic) = Topic(newId, original.title)
	}
}