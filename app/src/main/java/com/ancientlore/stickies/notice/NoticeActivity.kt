package com.ancientlore.stickies.notice

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.ancientlore.stickies.C.DUMMY_ID
import com.ancientlore.stickies.R

class NoticeActivity : AppCompatActivity() {

	companion object {
		const val EXTRA_NOTE_ID = "note_id"
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val noteId = getNoteIdArg()

		setContentView(R.layout.notice_activity)

		initUi(noteId)
	}

	private fun initUi(noteId: Long) {
		val fragment = supportFragmentManager.findFragmentById(R.id.content)
				?: NoticeFragment.newInstance(noteId)
		addFragment(fragment, R.id.content)
	}

	private fun addFragment(fragment: Fragment, id: Int) {
		supportFragmentManager.beginTransaction()
				.add(id, fragment)
				.commitNow()
	}

	private fun getNoteIdArg(): Long {
		return intent.getLongExtra(EXTRA_NOTE_ID, DUMMY_ID).takeIf { it != DUMMY_ID }
				?: throw RuntimeException("Error! Note's id is a mandatory argument!")
	}
}