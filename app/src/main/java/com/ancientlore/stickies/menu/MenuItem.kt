package com.ancientlore.stickies.menu

import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.DrawableRes
import android.support.annotation.IdRes
import android.support.annotation.StringRes

data class MenuItem(@IdRes val id: Int,
					@StringRes val title: Int,
					@DrawableRes val icon: Int = -1) : Parcelable {

	constructor(parcel: Parcel) : this(
			parcel.readInt(),
			parcel.readInt(),
			parcel.readInt())

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeInt(id)
		parcel.writeInt(title)
		parcel.writeInt(icon)
	}

	override fun describeContents() = 0

	companion object CREATOR : Parcelable.Creator<MenuItem> {
		override fun createFromParcel(parcel: Parcel) = MenuItem(parcel)

		override fun newArray(size: Int) = arrayOfNulls<MenuItem?>(size)
	}
}