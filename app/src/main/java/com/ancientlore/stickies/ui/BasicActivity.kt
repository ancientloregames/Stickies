package com.ancientlore.stickies.ui

import android.arch.lifecycle.ViewModel
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import com.ancientlore.stickies.R
import io.reactivex.subjects.PublishSubject

abstract class BasicActivity<T: ViewDataBinding, V: ViewModel>: AppCompatActivity() {
	private lateinit var viewDataBinding : T
	protected lateinit var viewModel : V

	protected val destroyEvent = PublishSubject.create<Any>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		viewDataBinding = DataBindingUtil.setContentView(this, getLayoutId())

		setSupportActionBar(findViewById(R.id.toolbar))
		supportActionBar?.title = getString(getTitleId())

		viewDataBinding.setLifecycleOwner(this)
		viewModel = createViewModel()
		viewDataBinding.setVariable(getBindingVariable(), viewModel)
		viewDataBinding.executePendingBindings()
	}

	override fun onDestroy() {
		destroyEvent.onNext(Any())

		super.onDestroy()
	}

	@LayoutRes
	abstract fun getLayoutId() : Int

	abstract fun getBindingVariable() : Int

	abstract fun createViewModel() : V

	@StringRes
	abstract fun getTitleId() : Int
}
