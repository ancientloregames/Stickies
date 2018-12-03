package com.ancientlore.stickies.data.source.remote

import android.util.Log
import com.ancientlore.stickies.SingletonHolder
import com.ancientlore.stickies.data.model.Topic
import com.ancientlore.stickies.data.source.DataSource
import com.ancientlore.stickies.data.source.EmptyResultException
import com.ancientlore.stickies.data.source.TopicsSource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class FirestoreTopicsSource private constructor(private val userId: String): TopicsSource {

	internal companion object : SingletonHolder<FirestoreTopicsSource, String>({ FirestoreTopicsSource(it) }) {
		private const val TAG = "FirestoreTopicsSource"

		private const val USER_DATA = "data"
		private const val USER_TOPICS = "topics"
	}

	private val db = FirebaseFirestore.getInstance()

	override fun getAllTopics(callback: DataSource.RequestCallback<List<Topic>>) {
		requestUserTopics().get()
				.addOnSuccessListener { snapshot ->
					deserialize(snapshot).takeIf { it.isNotEmpty() }
							?.let { callback.onSuccess(it) }
							?: callback.onFailure(EmptyResultException("$TAG: empty"))
				}
	}

	override fun getTopic(name: String, callback: DataSource.RequestCallback<Topic>) {
		requestUserTopic(name).get()
				.addOnSuccessListener { snapshot ->
					deserialize(snapshot)
							?.let { callback.onSuccess(it) }
							?:callback.onFailure(EmptyResultException("$TAG: no topic with name $name"))
				}
				.addOnFailureListener { callback.onFailure(it) }
	}

	override fun insertTopic(topic: Topic) {
		requestUserTopic(topic.name).set(topic)
	}

	override fun insertTopics(topics: List<Topic>) {
		topics.forEach { topic ->
			requestUserTopic(topic.name).set(topic)
		}
	}

	override fun reset(newTopics: List<Topic>) {
		deleteAllTopics()
		insertTopics(newTopics)
	}

	override fun deleteTopic(name: String) {
		requestUserTopic(name)
				.delete()
				.addOnSuccessListener { Log.d(TAG, "The note with name $name has been deleted") }
				.addOnFailureListener { Log.w(TAG, "Faild to delete the note with name $name", it) }
	}

	override fun deleteAllTopics() {
		val batch = db.batch()
		requestUserTopics().get().result?.forEach {
			batch.delete(it.reference)
		}
		batch.commit()
	}

	private fun requestUserTopics() = db.collection(USER_DATA).document(userId).collection(USER_TOPICS)

	private fun requestUserTopic(name: String) = db.collection(USER_DATA).document(userId).collection(USER_TOPICS).document(name)

	private fun deserialize(snapshot: DocumentSnapshot) = snapshot.toObject(Topic::class.java)

	private fun deserialize(snapshot: QuerySnapshot) = snapshot.toObjects(Topic::class.java)
}