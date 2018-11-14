package com.ancientlore.stickies.data.source.local

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.migration.Migration
import android.content.Context
import com.ancientlore.stickies.data.model.Note
import com.ancientlore.stickies.SingletonHolder
import com.ancientlore.stickies.data.model.Topic
import com.ancientlore.stickies.data.source.local.NotesDatabase.Companion.MIGRATION_1_2

@Database(entities = [Note::class, Topic::class], version = 2)
abstract class NotesDatabase : RoomDatabase() {

	abstract fun notesDao(): NotesDao

	abstract fun topicsDao(): TopicsDao

	companion object : SingletonHolder<NotesDatabase, Context>({
		Room.databaseBuilder(it, NotesDatabase::class.java, "notes.db")
				.addMigrations(MIGRATION_1_2)
				.build()
	}) {
		@JvmField val MIGRATION_1_2: Migration = object : Migration(1, 2) {
			override fun migrate(database: SupportSQLiteDatabase) {
				database.execSQL("CREATE TABLE topics(name TEXT PRIMARY KEY NOT NULL);")
			}
		}
	}
}