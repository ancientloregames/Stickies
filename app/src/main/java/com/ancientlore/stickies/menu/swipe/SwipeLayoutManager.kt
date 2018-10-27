package com.ancientlore.stickies.menu.swipe

import com.ancientlore.stickies.view.SwipeLayout
import java.lang.ref.WeakReference
import java.util.Collections
import java.util.HashMap

class SwipeLayoutManager(private val openOnlyOne: Boolean) {
	private val layouts = Collections.synchronizedMap(HashMap<String, WeakReference<SwipeLayout>>())

	private val lockerObject = Any()

	private val openCount: Int
		get() = synchronized(lockerObject) {
			return layouts.values.mapNotNull { it.get() }
					.filter { it.isMenuOpened }
					.count()
		}

	fun bind(swipeLayout: SwipeLayout, id: String) {
		layouts[id] = WeakReference(swipeLayout)

		swipeLayout.setOnSwipeListener(object : SwipeLayout.OnSwipeListener {

			override fun onBeginSwipe(swipeLayout: SwipeLayout, moveToRight: Boolean) {
				if (openOnlyOne)
					closeOthers(swipeLayout, true)
			}

			override fun onNotSwipe(swipeLayout: SwipeLayout) {
				closeAll(true)
			}

			override fun onSwipeClampReached(swipeLayout: SwipeLayout, moveToRight: Boolean) {
			}

			override fun onLeftStickyEdge(swipeLayout: SwipeLayout, moveToRight: Boolean) {
			}

			override fun onRightStickyEdge(swipeLayout: SwipeLayout, moveToRight: Boolean) {
			}
		})
	}

	fun hasOpened() = openCount > 0

	fun closeAll(animate: Boolean) {
		synchronized(lockerObject) {
			layouts.values.mapNotNull { it.get() }
					.forEach { reset(it, animate) }
		}
	}

	fun closeLayout(layout: SwipeLayout, animate: Boolean) {
		synchronized(lockerObject) {
			reset(layout, animate)
		}
	}

	fun closeLayout(id: String, animate: Boolean) {
		synchronized(lockerObject) {
			layouts[id]?.get()
					?.let { reset(it, animate) }
		}
	}

	fun closeOthers(excludedLayout: SwipeLayout, animate: Boolean) {
		synchronized(lockerObject) {
			layouts.values.mapNotNull { it.get() }
					.filter { it !== excludedLayout }
					.forEach { reset(it, animate) }
		}
	}

	private fun reset(layoutToClose: SwipeLayout, animate: Boolean) {
		if (animate)
			layoutToClose.animateReset()
		else
			layoutToClose.reset()
	}
}