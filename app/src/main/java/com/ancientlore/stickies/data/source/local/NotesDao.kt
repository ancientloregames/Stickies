package com.ancientlore.stickies.data.source.local

import android.arch.persistence.room.*
import com.ancientlore.stickies.data.model.Note

@Dao
interface NotesDao {

	@Query("SELECT * FROM notes")
	fun getAll(): List<Note>

	@Query("SELECT * FROM notes WHERE isImportant = 1")
	fun getImportant(): List<Note>

	@Query("SELECT * FROM notes WHERE id IN (:ids)")
	fun loadAllByIds(ids: LongArray): List<Note>

	@Query("SELECT * FROM notes WHERE id LIKE :id")
	fun findById(id: Long): Note?

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(note: Note): Long

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(note: List<Note>): LongArray

	@Update
	fun update(vararg note: Note)

	@Delete
	fun delete(note: Note)

	@Query("DELETE FROM notes WHERE id = :id")
	fun deleteById(id: Long)

	@Query("DELETE FROM notes")
	fun deleteAll()
}