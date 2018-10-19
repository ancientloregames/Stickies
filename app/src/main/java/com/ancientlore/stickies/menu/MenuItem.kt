package com.ancientlore.stickies.menu

import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.DrawableRes

data class MenuItem(val id: Int,
					val title: String,
					@DrawableRes val iconRes: Int = -1) : Parcelable {

	constructor(parcel: Parcel) : this(
			parcel.readInt(),
			parcel.readString() ?: "",
			parcel.readInt())

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeInt(id)
		parcel.writeString(title)
		parcel.writeInt(iconRes)
	}

	override fun describeContents() = 0

	companion object CREATOR : Parcelable.Creator<MenuItem> {
		override fun createFromParcel(parcel: Parcel) = MenuItem(parcel)

		override fun newArray(size: Int) = arrayOfNulls<MenuItem?>(size)
	}
}