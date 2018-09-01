package com.ancientlore.stickies

import com.ancientlore.stickies.databinding.ActivityMainBinding

class MainActivity : BasicActivity<ActivityMainBinding, MainActivityViewModel>() {

	override fun getLayoutId() = R.layout.activity_main

	override fun getBindingVariable() = BR.viewModel

	override fun createViewModel() = MainActivityViewModel(application)

	override fun getTitleId() = R.string.app_name
}
