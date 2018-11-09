package com.ancientlore.stickies.notice

import android.app.Application
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ancientlore.stickies.C.DUMMY_ID
import com.ancientlore.stickies.R
import com.ancientlore.stickies.databinding.NoticeUiBinding
import com.ancientlore.stickies.notedetail.NoteDetailViewModel

class NoticeFragment: Fragment() {
	companion object {
		private const val ARG_NOTE_ID = "note_id"

		fun newInstance(noteId: Long): NoticeFragment {
			return NoticeFragment().apply {
				arguments = Bundle().apply {
					putLong(ARG_NOTE_ID, noteId)
				}
			}
		}
	}

	private lateinit var viewModel: NoteDetailViewModel

	@LayoutRes private fun getLayoutId() = R.layout.notice_ui

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
			inflater.inflate(getLayoutId(), container, false)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		viewModel = createViewModel(activity!!.application, getNoteIdArg())

		bind(view, viewModel)
	}

	private fun createViewModel(application: Application, noteId: Long) = NoteDetailViewModel(application, noteId)

	private fun bind(view: View, viewModel: NoteDetailViewModel) {
		val binding = NoticeUiBinding.bind(view)
		binding.viewModel = viewModel
	}

	private fun getNoteIdArg(): Long {
		return arguments?.getLong(ARG_NOTE_ID, DUMMY_ID).takeIf { it != DUMMY_ID }
				?: throw RuntimeException("Error! Note's id is a mandatory argument!")
	}
}