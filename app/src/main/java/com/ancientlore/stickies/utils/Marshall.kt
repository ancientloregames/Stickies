package com.ancientlore.stickies.utils

import android.os.Parcel
import android.os.Parcelable
import java.util.*

fun Parcelable.marshall() : ByteArray {
	val parcel = Parcel.obtain()
	writeToParcel(parcel, 0)
	val bytes = parcel.marshall()
	parcel.recycle()
	return bytes
}

fun <T> ByteArray.unmarshall(creator: Parcelable.Creator<T>): T {
	val parcel = unmarshall()
	val result = creator.createFromParcel(parcel)
	parcel.recycle()
	return result
}

fun <T> List<ByteArray>.unmarshall(creator: Parcelable.Creator<T>) = map { it.unmarshall(creator) }

fun ByteArray.split(delimiter: ByteArray): List<ByteArray> {
	val list = mutableListOf<ByteArray>()
	var blockStart = 0
	var i = 0
	while (i < size) {
		if (isMatching(delimiter, i)) {
			list.add(Arrays.copyOfRange(this, blockStart, i))
			blockStart = i + delimiter.size
			i = blockStart
		}
		i++
	}
	if (size > blockStart)
		list.add(Arrays.copyOfRange(this, blockStart, size))
	return list
}

fun ByteArray.isMatching(delimiter: ByteArray, pos: Int): Boolean {
	for (i in 0 until delimiter.size) {
		if (delimiter[i] != this[pos + i])
			return false
	}
	return true
}

private fun ByteArray.unmarshall(): Parcel {
	val parcel = Parcel.obtain()
	parcel.unmarshall(this, 0, size)
	parcel.setDataPosition(0) // This is extremely important!
	return parcel
}