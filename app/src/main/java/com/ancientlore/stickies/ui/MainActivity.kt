package com.ancientlore.stickies.ui

import com.ancientlore.stickies.BR
import com.ancientlore.stickies.R
import com.ancientlore.stickies.databinding.ActivityMainBinding
import com.ancientlore.stickies.viewmodel.MainActivityViewModel

class MainActivity : BasicActivity<ActivityMainBinding, MainActivityViewModel>() {

	override fun getLayoutId() = R.layout.activity_main

	override fun getBindingVariable() = BR.viewModel

	override fun createViewModel() = MainActivityViewModel(application)

	override fun getTitleId() = R.string.app_name
}
