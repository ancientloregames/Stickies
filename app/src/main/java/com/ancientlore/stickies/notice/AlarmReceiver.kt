package com.ancientlore.stickies.notice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle

class AlarmReceiver: BroadcastReceiver() {
	companion object {
		const val DEFAULT_CHANNEL_ID = "notes"

		const val EXTRA_NOTE_ID = "note_id"
	}

	override fun onReceive(context: Context, intent: Intent) {
		getIdAndShow(context, intent.extras)
	}

	private fun getIdAndShow(context: Context, extras: Bundle?) {
		extras?.getLong(EXTRA_NOTE_ID)
				?.takeIf { it > 0 }
				?.let { showNoticeActivity(context, it) }
	}

	private fun showNoticeActivity(context: Context, noticeId: Long) {
		Intent(context, NoticeActivity::class.java).run {
			flags = Intent.FLAG_ACTIVITY_NEW_TASK
			putExtra(EXTRA_NOTE_ID, noticeId)
			context.startActivity(this)
		}
	}
}