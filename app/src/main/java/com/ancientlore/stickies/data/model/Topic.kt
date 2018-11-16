package com.ancientlore.stickies.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable

@Entity(tableName = "topics")
data class Topic(@field:PrimaryKey var name: String) : Parcelable {

	companion object CREATOR : Parcelable.Creator<Topic> {
		override fun createFromParcel(parcel: Parcel) = Topic(parcel)

		override fun newArray(size: Int) = arrayOfNulls<Topic>(size)
	}

	constructor(parcel: Parcel) : this(parcel.readString())

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(name)
	}

	override fun describeContents() = 0
}