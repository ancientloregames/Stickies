package com.ancientlore.stickies.data.source.local

import android.arch.persistence.room.*
import com.ancientlore.stickies.data.model.Topic

@Dao
interface TopicsDao {

	@Query("SELECT * FROM topics")
	fun getAll(): List<Topic>

	@Query("SELECT * FROM topics WHERE name LIKE :name")
	fun findById(name: String): Topic?

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	fun insert(topic: Topic)

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	fun insert(topics: List<Topic>)

	@Update
	fun update(vararg topic: Topic)

	@Delete
	fun delete(topic: Topic)

	@Query("DELETE FROM topics WHERE name = :name")
	fun deleteById(name: String)

	@Query("DELETE FROM topics")
	fun deleteAll()
}