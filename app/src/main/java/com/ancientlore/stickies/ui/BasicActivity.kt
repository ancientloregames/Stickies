package com.ancientlore.stickies.ui

import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import com.ancientlore.stickies.R
import com.ancientlore.stickies.viewmodel.BasicViewModel
import io.reactivex.subjects.PublishSubject

abstract class BasicActivity<T: ViewDataBinding, V: BasicViewModel>: AppCompatActivity() {
	private lateinit var viewDataBinding : T
	protected lateinit var viewModel : V

	protected val destroyEvent = PublishSubject.create<Any>()

	@LayoutRes
	abstract fun getLayoutId() : Int

	abstract fun getBindingVariable() : Int

	abstract fun createViewModel() : V

	@StringRes
	abstract fun getTitleId() : Int

	final override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		viewDataBinding = DataBindingUtil.setContentView(this, getLayoutId())

		setupView(savedInstanceState)

		setupViewModel()
	}

	override fun onDestroy() {
		destroyEvent.onNext(Any())

		super.onDestroy()
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		viewModel.handleActivityResult(requestCode, resultCode, data)
	}

	/**
	 * Called before the initialization of the ViewModel
	 */
	@CallSuper
	protected open fun setupView(savedInstanceState: Bundle?) {
		setupActionBar()
	}

	private fun setupActionBar() {
		setSupportActionBar(findViewById(R.id.toolbar))
		supportActionBar?.title = getString(getTitleId())
	}

	@CallSuper
	protected open fun setupViewModel() {
		viewDataBinding.setLifecycleOwner(this)
		viewModel = createViewModel()
		viewDataBinding.setVariable(getBindingVariable(), viewModel)
		viewDataBinding.executePendingBindings()
	}
}
