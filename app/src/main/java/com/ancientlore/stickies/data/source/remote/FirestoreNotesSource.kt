package com.ancientlore.stickies.data.source.remote

import android.util.Log
import com.ancientlore.stickies.SingletonHolder
import com.ancientlore.stickies.data.model.Note
import com.ancientlore.stickies.data.model.Topic
import com.ancientlore.stickies.data.source.DataSource
import com.ancientlore.stickies.data.source.EmptyResultException
import com.ancientlore.stickies.data.source.NotesSource
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreNotesSource private constructor(private val user: FirebaseUser): NotesSource {

	internal companion object : SingletonHolder<FirestoreNotesSource, FirebaseUser>({ FirestoreNotesSource(it) }) {
		const val USER_DATA = "data"
		const val USER_NOTES = "notes"

		private const val FIELD_IMPORTANT = "isImportant"
		private const val FIELD_COMPLETED = "isCompleted"
		private const val FIELD_TOPIC = "topic"
	}

	private val db = FirebaseFirestore.getInstance()

	override fun getAll(callback: DataSource.RequestCallback<List<Note>>) {
		requestUserNotes().get()
				.addOnSuccessListener { snapshot ->
					snapshot.toObjects(Note::class.java)
							.takeIf { it.isNotEmpty() }
							?.let { callback.onSuccess(it) }
							?: callback.onFailure(EmptyResultException())
				}
				.addOnFailureListener { callback.onFailure(it) }
	}

	override fun getImportant(callback: DataSource.RequestCallback<List<Note>>) {
		requestUserNotes()
				.whereEqualTo(FIELD_IMPORTANT, true).get()
				.addOnSuccessListener { snapshot ->
					snapshot.toObjects(Note::class.java)
							.takeIf { it.isNotEmpty() }
							?.let { callback.onSuccess(it) }
							?: callback.onFailure(EmptyResultException())
				}
				.addOnFailureListener { callback.onFailure(it) }
	}

	override fun getAllByTopic(topic: Topic, callback: DataSource.RequestCallback<List<Note>>) {
		requestUserNotes()
				.whereEqualTo(FIELD_TOPIC, topic.name).get()
				.addOnSuccessListener { snapshot ->
					snapshot.toObjects(Note::class.java)
							.takeIf { it.isNotEmpty() }
							?.let { callback.onSuccess(it) }
							?: callback.onFailure(EmptyResultException())
				}
				.addOnFailureListener { callback.onFailure(it) }
	}

	override fun getItem(id: Long, callback: DataSource.RequestCallback<Note>) {
		requestUserNote(id).get()
				.addOnSuccessListener { snapshot ->
					snapshot.toObject(Note::class.java)?.let {
						callback.onSuccess(it)
					} ?: callback.onFailure(EmptyResultException())
				}
				.addOnFailureListener { callback.onFailure(it) }
	}

	override fun insertItem(item: Note, callback: DataSource.RequestCallback<Long>) {
		requestUserNote(item.id)
				.set(item)
				.addOnSuccessListener { callback.onSuccess(item.id) }
				.addOnFailureListener { callback.onFailure(it) }
	}

	override fun updateItem(item: Note) {
		requestUserNote(item.id)
				.set(item)
				.addOnSuccessListener {
					Log.d("FirestoreNotesSource", "The note with id ${item.id} has been updated")
				}
				.addOnFailureListener {
					Log.w("FirestoreNotesSource", "Faild to update the note with id ${item.id}")
				}
	}

	override fun reset(newItems: List<Note>) {
		deleteAll()
		requestUserNotes().add(newItems)
	}

	override fun deleteAll() {
		val batch = db.batch()
		requestUserNotes().get().result?.forEach {
			batch.delete(it.reference)
		}
		batch.commit()
	}

	override fun deleteItem(id: Long) {
		requestUserNote(id)
				.delete()
				.addOnSuccessListener {
					Log.d("FirestoreNotesSource", "The note with id $id has been deleted")
				}
				.addOnFailureListener {
					Log.w("FirestoreNotesSource", "Faild to delete the note with id $id")
				}
	}

	override fun switchImportance(id: Long, isImportant: Boolean) {
		requestUserNote(id)
				.update(FIELD_IMPORTANT, isImportant)
				.addOnSuccessListener {
					Log.d("FirestoreNotesSource", "The note's $id importance has been set to $isImportant")
				}
				.addOnFailureListener {
					Log.w("FirestoreNotesSource", "Faild to update the note's $id importance")
				}
	}

	override fun switchCompletion(id: Long, isCompleted: Boolean) {
		requestUserNote(id)
				.update(FIELD_COMPLETED, isCompleted)
				.addOnSuccessListener {
					Log.d("FirestoreNotesSource", "The note's $id completion has been set to $isCompleted")
				}
				.addOnFailureListener {
					Log.w("FirestoreNotesSource", "Faild to update the note's $id completion")
				}
	}

	private fun requestUserNotes() = db.collection(USER_DATA).document(user.uid).collection(USER_NOTES)

	private fun requestUserNote(id: Long) = db.collection(USER_DATA).document(user.uid).collection(USER_NOTES).document(id.toString())
}