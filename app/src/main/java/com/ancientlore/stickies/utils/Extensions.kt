package com.ancientlore.stickies.utils

import android.content.Context
import android.text.Html
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.ancientlore.stickies.R
import com.ancientlore.stickies.data.model.Note

fun Context.showKeybouard(view: View) = showKeybouard(view, 0)

fun Context.showKeybouard(view: View, flags: Int) = view.requestFocus() &&
		(getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(view, flags)

fun Context.hideKeyboard(view: View) = hideKeyboard(view, 0)

fun Context.hideKeyboard(view: View, flags: Int) =
		(getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(view.windowToken, flags)

fun Note.getText(): String? {
	return when {
		title.isNotEmpty() -> title
		body.isNotEmpty() -> plainBody
		else -> {
			Log.e("Note", "Error! Note $id have nither title nor body")
			null
		}
	}
}

fun Note.getListTitle(context: Context): String = getText() ?: context.getString(R.string.note_num, id)

fun Note.getTitle(context: Context): String = if (title.isNotEmpty()) title else context.getString(R.string.note_num, id)

fun String.toPlainText() = Html.fromHtml(this).toString()