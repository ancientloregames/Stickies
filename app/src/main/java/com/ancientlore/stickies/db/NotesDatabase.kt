package com.ancientlore.stickies.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.ancientlore.stickies.SingletonHolder

@Database(entities = [(Note::class)], version = 1)
abstract class NotesDatabase : RoomDatabase() {

	abstract fun noteDao(): NoteDao

	companion object : SingletonHolder<NotesDatabase, Context>({
		Room.databaseBuilder(it, NotesDatabase::class.java, "notes.db").build()
	})
}