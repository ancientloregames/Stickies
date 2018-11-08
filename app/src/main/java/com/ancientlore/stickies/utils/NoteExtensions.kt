package com.ancientlore.stickies.utils

import android.content.Context
import android.text.Html
import android.text.Spanned
import android.util.Log
import com.ancientlore.stickies.R
import com.ancientlore.stickies.data.model.Note

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
fun Note.spannedBody(): Spanned = Html.fromHtml(body)