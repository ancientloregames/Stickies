package com.ancientlore.stickies

import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import io.reactivex.internal.disposables.ListCompositeDisposable
import kotlinx.android.synthetic.main.appbar.*

abstract class BasicActivity<T: ViewDataBinding, V: BasicViewModel>: AppCompatActivity() {
	private lateinit var viewDataBinding : T
	protected lateinit var viewModel : V

	protected val subscriptions = ListCompositeDisposable()

	@LayoutRes
	abstract fun getLayoutId() : Int

	abstract fun getBindingVariable() : Int

	abstract fun createViewModel() : V

	final override fun onCreate(savedInstanceState: Bundle?) {
		AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
		super.onCreate(savedInstanceState)
		viewDataBinding = DataBindingUtil.setContentView(this, getLayoutId())

		setupView(savedInstanceState)

		setupViewModel()
	}

	final override fun onDestroy() {
		subscriptions.dispose()

		super.onDestroy()
	}

	override fun onSaveInstanceState(outState: Bundle?) {
		outState?.run { viewModel.saveState(this) }
		super.onSaveInstanceState(outState)
	}

	override fun onRestoreInstanceState(prevState: Bundle?) {
		prevState?.run { viewModel.loadState(this) }
		super.onRestoreInstanceState(prevState)
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

	@CallSuper
	protected open fun setupActionBar() = setSupportActionBar(toolbar)

	@CallSuper
	protected open fun setupViewModel() {
		viewDataBinding.setLifecycleOwner(this)
		viewModel = createViewModel()
		viewDataBinding.setVariable(getBindingVariable(), viewModel)
		viewDataBinding.executePendingBindings()
	}
}
