package com.ancientlore.stickies.menu

import android.support.annotation.DrawableRes

data class MenuItem(val id: Int,
					val title: String,
					@DrawableRes val iconRes: Int = -1)