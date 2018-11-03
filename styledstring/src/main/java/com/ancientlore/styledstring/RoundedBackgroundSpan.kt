package com.ancientlore.styledstring

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.style.LineHeightSpan
import android.text.style.ReplacementSpan

/**
 * It is imperative to implement LineHeightSpan, the draw method won't be called without it
 */
class RoundedBackgroundSpan(private val textColor: Int = Color.BLACK,
							private val backgroundColor: Int,
							private val cornerRadius: Int = CORNER_RADIUS )
	: ReplacementSpan(), LineHeightSpan {

	override fun draw(canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
		val textLength = x + paint.measureText(text, start, end)

		paint.color = backgroundColor
		val badge = RectF(x, top.toFloat() + SPAN_MARGIN_Y, textLength + SPAN_SHIFT, bottom.toFloat() - SPAN_MARGIN_Y)
		canvas.drawRoundRect(badge, cornerRadius.toFloat(), cornerRadius.toFloat(), paint)

		paint.color = textColor
		canvas.drawText(text, start, end, x + TEXT_SHIFT, y.toFloat(), paint)
	}

	override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?) =
			Math.round(paint.measureText(text, start, end)) + SPAN_SHIFT

	override fun chooseHeight(text: CharSequence?, start: Int, end: Int, spanstartv: Int, v: Int, fm: Paint.FontMetricsInt?) {}

	companion object {
		private const val CORNER_RADIUS = 40
		private const val SPAN_SHIFT = 40
		private const val TEXT_SHIFT = SPAN_SHIFT / 2
		private const val SPAN_MARGIN_Y = 5
	}
}