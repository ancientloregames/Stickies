package com.ancientlore.stickies.notice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.ancientlore.stickies.utils.cancelReminder

class NoticeActionReceiver: BroadcastReceiver() {
	companion object {
		const val ACTION_CANCEL_NOTICE = "com.ancientlore.stickies.action.CANCEL_NOTICE"

		const val EXTRA_NOTE_ID = "note_id"
	}
	override fun onReceive(context: Context, intent: Intent) {
		when (intent.action) {
			ACTION_CANCEL_NOTICE -> getIdAndCancel(context, intent.extras)
		}
	}

	private fun getIdAndCancel(context: Context, extras: Bundle?) {
		extras?.getInt(EXTRA_NOTE_ID)
				?.takeIf { it > 0 }
				?.let { cancelNotice(context, it) }
	}

	private fun cancelNotice(context: Context, id: Int) {
		context.cancelReminder(id, AlarmReceiver::class.java)
	}
}