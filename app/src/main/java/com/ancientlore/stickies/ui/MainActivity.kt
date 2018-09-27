package com.ancientlore.stickies.ui

import android.content.Intent
import android.os.Bundle
import com.ancientlore.stickies.BR
import com.ancientlore.stickies.R
import com.ancientlore.stickies.databinding.ActivityMainBinding
import com.ancientlore.stickies.viewmodel.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BasicActivity<ActivityMainBinding, MainActivityViewModel>() {

	override fun getLayoutId() = R.layout.activity_main

	override fun getBindingVariable() = BR.viewModel

	override fun createViewModel() = MainActivityViewModel(application, getListAdapter())

	override fun getTitleId() = R.string.app_name

	override fun setupView(savedInstanceState: Bundle?) {
		super.setupView(savedInstanceState)

		setupList()
	}

	override fun setupViewModel() {
		super.setupViewModel()

		viewModel.onAddNote()
				.takeUntil(destroyEvent)
				.subscribe { startNoteAddition(it) }
	}

	private fun setupList() {
		val listAdapter = NotesListAdapter(this, mutableListOf())
		notesListView.adapter = listAdapter
	}

	private fun getListAdapter() = notesListView.adapter as NotesListAdapter

	private fun startNoteAddition(requestCode: Int) {
		val intent = Intent(this, NoteActivity::class.java)
		startActivityForResult(intent, requestCode)
	}
}
