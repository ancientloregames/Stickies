package com.ancientlore.stickies.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(@PrimaryKey(autoGenerate = true) var id: Long = 0,
				@field:ColumnInfo var title: String = "",
				@field:ColumnInfo var body: String = "")