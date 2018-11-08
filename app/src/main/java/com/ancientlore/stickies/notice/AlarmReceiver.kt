package com.ancientlore.stickies.notice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver: BroadcastReceiver() {
	companion object {
		const val DEFAULT_CHANNEL_ID = "notes"

		const val EXTRA_NOTE = "note"
	}

	override fun onReceive(context: Context?, intent: Intent?) {
	}
}