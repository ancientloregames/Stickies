package com.ancientlore.stickies.data.source.local

import android.arch.persistence.room.*
import com.ancientlore.stickies.data.model.Topic

@Dao
interface TopicsDao {

	@Query("SELECT * FROM topics")
	fun getAll(): List<Topic>

	@Query("SELECT * FROM topics WHERE id LIKE :id")
	fun findById(id: Long): Topic?

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(note: Topic): Long

	@Update
	fun update(vararg note: Topic)

	@Delete
	fun delete(note: Topic)

	@Query("DELETE FROM topics WHERE id = :id")
	fun deleteById(id: Long)

	@Query("DELETE FROM notes")
	fun deleteAll()
}