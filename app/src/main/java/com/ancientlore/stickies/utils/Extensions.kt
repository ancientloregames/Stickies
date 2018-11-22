package com.ancientlore.stickies.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.text.Html
import android.view.View
import android.view.inputmethod.InputMethodManager

fun Context.showKeybouard(view: View) = showKeybouard(view, 0)

fun Context.showKeybouard(view: View, flags: Int) = view.requestFocus() &&
		(getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(view, flags)

fun Context.hideKeyboard(view: View) = hideKeyboard(view, 0)

fun Context.hideKeyboard(view: View, flags: Int) =
		(getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(view.windowToken, flags)

fun Activity.tryStartActivity(intent: Intent, requestCode: Int): Boolean {
	val activityExists = intent.isResolvable(packageManager)

	if (activityExists) startActivityForResult(intent, requestCode)

	return activityExists
}

fun Intent.isResolvable(packageManager: PackageManager) = resolveActivity(packageManager) != null

fun String.toPlainText() = Html.fromHtml(this).toString()