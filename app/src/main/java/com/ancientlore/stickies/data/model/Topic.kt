package com.ancientlore.stickies.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "topics")
data class Topic(@field:PrimaryKey var name: String)