package com.ancientlore.styledstring

import android.text.Selection
import android.text.Spannable
import android.text.method.BaseMovementMethod
import android.text.style.ClickableSpan
import android.view.MotionEvent
import android.widget.TextView

object ClickableMovementMethod : BaseMovementMethod() {

	override fun initialize(widget: TextView, text: Spannable) { Selection.removeSelection(text) }

	override fun canSelectArbitrarily() = false

	override fun onTouchEvent(view: TextView, buffer: Spannable, event: MotionEvent): Boolean {
		val action = event.actionMasked
		if ((action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN)
				&& view.paint.measureText(view.text.toString()) >= event.x) {
			val spanOffset = getSpanOffset(view, event)

			getClickableSpan(buffer, spanOffset)?.let {
				if (action != MotionEvent.ACTION_UP)
					Selection.setSelection(buffer, buffer.getSpanStart(it), buffer.getSpanEnd(it))
				else it.onClick(view)

				return true
			} ?: Selection.removeSelection(buffer)
		}

		return false
	}

	private fun getClickableSpan(buffer: Spannable, offset: Int) =
			buffer.getSpans(offset, offset, ClickableSpan::class.java).firstOrNull()

	private fun getSpanOffset(view: TextView, event: MotionEvent): Int {
		val x = event.x.toInt() - view.totalPaddingLeft + view.scrollX
		val y = event.y.toInt() - view.totalPaddingTop + view.scrollY

		val line = view.layout.getLineForVertical(y)

		return view.layout.getOffsetForHorizontal(line, x.toFloat())
	}
}
