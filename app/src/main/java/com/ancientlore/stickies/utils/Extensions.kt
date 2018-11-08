package com.ancientlore.stickies.utils

import android.content.Context
import android.text.Html
import android.view.View
import android.view.inputmethod.InputMethodManager

fun Context.showKeybouard(view: View) = showKeybouard(view, 0)

fun Context.showKeybouard(view: View, flags: Int) = view.requestFocus() &&
		(getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(view, flags)

fun Context.hideKeyboard(view: View) = hideKeyboard(view, 0)

fun Context.hideKeyboard(view: View, flags: Int) =
		(getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(view.windowToken, flags)

fun String.toPlainText() = Html.fromHtml(this).toString()