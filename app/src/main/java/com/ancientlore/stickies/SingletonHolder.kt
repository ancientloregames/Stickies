package com.ancientlore.stickies

open class SingletonHolder<out T, in A>(creator: (A) -> T) {
	private var creator: ((A) -> T)? = creator
	@Volatile private var instance: T? = null

	fun getInstance(arg: A): T {
		instance?.let { return it }

		return synchronized(this) {
			instance ?:let {
				val created = creator!!(arg)
				instance = created
				creator = null
				created
			}
		}
	}
}