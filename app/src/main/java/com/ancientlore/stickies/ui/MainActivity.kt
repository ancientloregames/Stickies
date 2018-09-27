package com.ancientlore.stickies.ui

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

	private fun setupList() {
		val listAdapter = NotesListAdapter(this, mutableListOf())
		alarmListView.adapter = listAdapter
	}

	private fun getListAdapter() = alarmListView.adapter as NotesListAdapter
}
