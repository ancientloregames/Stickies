package com.ancientlore.stickies.data.source.remote

import com.ancientlore.stickies.SingletonHolder
import com.ancientlore.stickies.data.model.Note
import com.ancientlore.stickies.data.model.Topic
import com.ancientlore.stickies.data.source.DataSource
import com.ancientlore.stickies.data.source.NotesSource
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreNotesSource private constructor(private val user: FirebaseUser): NotesSource {

	internal companion object : SingletonHolder<FirestoreNotesSource, FirebaseUser>({ FirestoreNotesSource(it) }) {
		const val USER_DATA = "data"
		const val USER_NOTES = "notes"
	}

	private val db = FirebaseFirestore.getInstance()

	override fun getAll(callback: DataSource.RequestCallback<List<Note>>) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun getImportant(callback: DataSource.RequestCallback<List<Note>>) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun getAllByTopic(topic: Topic, callback: DataSource.RequestCallback<List<Note>>) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun getItem(id: Long, callback: DataSource.RequestCallback<Note>) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun insertItem(item: Note, callback: DataSource.RequestCallback<Long>) {
		db.collection(USER_DATA).document(user.uid)
				.collection(USER_NOTES).document(item.id.toString())
				.set(item)
				.addOnSuccessListener { callback.onSuccess(item.id) }
				.addOnFailureListener { callback.onFailure(it) }
	}

	override fun updateItem(item: Note) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun reset(newItems: List<Note>) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun deleteAll() {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun deleteItem(id: Long) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun switchImportance(id: Long, isImportant: Boolean) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun switchCompletion(id: Long, isCompleted: Boolean) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
}