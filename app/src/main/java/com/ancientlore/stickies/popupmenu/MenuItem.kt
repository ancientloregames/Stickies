package com.ancientlore.stickies.popupmenu

import android.support.annotation.DrawableRes

data class MenuItem(val id: Int,
					val title: String,
					@DrawableRes val iconRes: Int = -1)