package com.ancientlore.styledstring

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.FloatRange
import android.support.annotation.FontRes
import android.support.annotation.RequiresApi
import android.support.v4.content.res.ResourcesCompat
import android.text.Annotation
import android.text.SpannableString
import android.text.Spanned
import android.text.style.*
import android.widget.TextView
import java.util.regex.Pattern

class StyledString(private val text: CharSequence): SpannableString(text) {

	data class Range(val start: Int, val end: Int)

	private val ranges = mutableListOf<Range>()

	private var spanMode = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE

	/**
	 * Select the exact range
	 */
	fun forRange(start: Int, end: Int): StyledString {
		ranges.clear()
		if (isValidPos(start) && isValidPos(end))
			ranges.add(Range(start, end))
		return this
	}

	/**
	 * Select the exact list of ranges
	 */
	fun forRanges(ranges: List<Range>): StyledString {
		this.ranges.clear()
		this.ranges.addAll(ranges)
		return this
	}

	/**
	 * Select the whole text as range
	 */
	fun forAll(): StyledString {
		ranges.clear()
		val range = Range(0, text.length)
		ranges.add(range)
		return this
	}

	/**
	 * Find and store all ranges in the initial string, that represents the exact word
	 */
	fun forAll(word: String): StyledString {
		ranges.clear()
		val pattern = Pattern.quote(word)
		ranges.addAll(getRanges(pattern))
		return this
	}

	/**
	 * Find and store all ranges in the initial string, that starts with prefix
	 */
	fun forAllStartWith(prefix: String): StyledString {
		ranges.clear()
		val pattern = Pattern.quote(prefix) + "\\w+"
		ranges.addAll(getRanges(pattern))
		return this
	}

	/**
	 * Find and store all ranges in the initial string, that ends with sufix
	 */
	fun forAllEndWith(sufix: String): StyledString {
		ranges.clear()
		val pattern = "\\w+" + Pattern.quote(sufix)
		ranges.addAll(getRanges(pattern))
		return this
	}

	/**
	 * Find and store a range in the initial string, that represents the fisrt occurrence of the word
	 */
	fun forFirst(word: String): StyledString {
		ranges.clear()
		val pattern = Pattern.quote(word)
		getRanges(pattern).firstOrNull()?.let {
			ranges.add(it)
		}
		return this
	}

	/**
	 * Find and store a range in the initial string, that represents the last occurrence of the word
	 */
	fun forLast(word: String): StyledString {
		ranges.clear()
		val pattern = Pattern.quote(word)
		getRanges(pattern).lastOrNull()?.let {
			ranges.add(it)
		}
		return this
	}

	/**
	 * Find and store a range in the initial string, that represents the fisrt occurrence of the word with prefix
	 */
	fun forFirstWith(prefix: String): StyledString {
		ranges.clear()
		val pattern = Pattern.quote(prefix) + "\\w+"
		getRanges(pattern).firstOrNull()?.let {
			ranges.add(it)
		}
		return this
	}

	/**
	 * Sets actions on the clicked text ranges, that was previously detected
	 */
	fun doOnClick(view: TextView, action: OnSpanClickListener): StyledString {
		makeTagsClickable(view)
		ranges.forEach {
			val spanText = subSequence(it.start, it.end)
			setSpan(ClickableTextSpan(spanText, action), it.start, it.end, spanMode)
		}
		return this
	}

	/**
	 * Opens an external url on the clicked text ranges, that was previously detected
	 */
	fun setLink(view: TextView, url: String) = setLink(view, Uri.parse(url))

	/**
	 * Opens an external uri on the clicked text ranges, that was previously detected
	 */
	fun setLink(view: TextView, uri: Uri): StyledString {
		makeTagsClickable(view)
		ranges.forEach {
			val spanText = subSequence(it.start, it.end)
			val listener = createUrlListener(view.context, uri)
			setSpan(ClickableTextSpan(spanText, listener), it.start, it.end, spanMode)
		}
		return this
	}

	/**
	 * Applies text color for all ranges, that was previously detected
	 */
	fun applyTextColor(@ColorInt color: Int): StyledString {
		ranges.forEach {
			setSpan(ForegroundColorSpan(color), it.start, it.end, spanMode)
		}
		return this
	}

	/**
	 * Applies background color for all ranges, that was previously detected
	 */
	fun applyBackColor(@ColorInt color: Int): StyledString {
		ranges.forEach {
			setSpan(BackgroundColorSpan(color), it.start, it.end, spanMode)
		}
		return this
	}

	/**
	 * Applies background color in rounded span for all ranges, that was previously detected
	 */
	fun applyBackColor(@ColorInt backColor: Int, cornerRadius: Int): StyledString {
		ranges.forEach {
			setSpan(RoundedBackgroundSpan(backgroundColor = backColor, cornerRadius = cornerRadius), it.start, it.end, spanMode)
		}
		return this
	}

	/**
	 * Applies the specified font family for all ranges, that was previously detected
	 */
	fun setFont(fontFamily: String): StyledString {
		ranges.forEach {
			setSpan(TypefaceSpan(fontFamily), it.start, it.end, spanMode)
		}
		return this
	}

	/**
	 * Applies the specified font for all ranges, that was previously detected
	 */
	@RequiresApi(Build.VERSION_CODES.P)
	fun setFont(typeface: Typeface): StyledString {
		ranges.forEach {
			setSpan(TypefaceSpan(typeface), it.start, it.end, spanMode)
		}
		return this
	}

	/**
	 * Applies the specified font for all ranges, that was previously detected
	 */
	@RequiresApi(Build.VERSION_CODES.P)
	fun setFont(context: Context, @FontRes fontRes: Int): StyledString {
		val typeface = ResourcesCompat.getFont(context, fontRes)
		ranges.forEach {
			setSpan(TypefaceSpan(typeface), it.start, it.end, spanMode)
		}
		return this
	}

	/**
	 * Makes all ranges, that was previously detected, bold
	 */
	fun makeBold(): StyledString {
		ranges.forEach {
			setSpan(StyleSpan(Typeface.BOLD), it.start, it.end, spanMode)
		}
		return this
	}

	/**
	 * Makes all ranges, that was previously detected, italic
	 */
	fun makeItalic(): StyledString {
		ranges.forEach {
			setSpan(StyleSpan(Typeface.ITALIC), it.start, it.end, spanMode)
		}
		return this
	}

	/**
	 * Makes all ranges, that was previously detected, normal
	 */
	fun makeNormal(): StyledString {
		ranges.forEach {
			setSpan(StyleSpan(Typeface.NORMAL), it.start, it.end, spanMode)
		}
		return this
	}

	/**
	 * Makes all ranges, that was previously detected, underlined
	 */
	fun makeUnderlined(): StyledString {
		ranges.forEach {
			setSpan(UnderlineSpan(), it.start, it.end, spanMode)
		}
		return this
	}

	/**
	 * Makes all ranges, that was previously detected, crossed out
	 */
	fun crossOut(): StyledString {
		ranges.forEach {
			setSpan(StrikethroughSpan(), it.start, it.end, spanMode)
		}
		return this
	}

	/**
	 * Set the text size for all ranges, that was previously detected
	 */
	fun setSize(size: Int): StyledString {
		ranges.forEach {
			setSpan(AbsoluteSizeSpan(size), it.start, it.end, spanMode)
		}
		return this
	}

	/**
	 * Set the text size in dp for all ranges, that was previously detected
	 */
	fun setSizeDp(size: Int): StyledString {
		ranges.forEach {
			setSpan(AbsoluteSizeSpan(size, true), it.start, it.end, spanMode)
		}
		return this
	}

	/**
	 * Set the text size to some proportion based on current size, that was previously detected
	 */
	fun setScaledSize(@FloatRange(from = .0) scale: Float): StyledString {
		ranges.forEach {
			setSpan(RelativeSizeSpan(scale), it.start, it.end, spanMode)
		}
		return this
	}

	/**
	 * Makes all ranges, that was previously detected, annotated
	 */
	fun annotate(key: String, value: String): StyledString {
		ranges.forEach {
			setSpan(Annotation(key, value), it.start, it.end, spanMode)
		}
		return this
	}

	/**
	 *  Moves the position of the text baseline higher for the ranges, that were previously detected
	 */
	fun makeSuperscript(): StyledString {
		ranges.forEach {
			setSpan(SuperscriptSpan(), it.start, it.end, spanMode)
		}
		return this
	}

	/**
	 *  Moves the position of the text baseline lower for the ranges, that were previously detected
	 */
	fun makeSubscript(): StyledString {
		ranges.forEach {
			setSpan(SubscriptSpan(), it.start, it.end, spanMode)
		}
		return this
	}

	fun setSpanMode(spanMode: Int): StyledString {
		this.spanMode = spanMode
		return this
	}

	fun removeStyles(clazz: Class<*>): StyledString {
		removeSpan(clazz)
		return this
	}

	private fun getRanges(pattern: String): List<Range> {
		val ranges = mutableListOf<Range>()
		val matcher = Pattern.compile(pattern).matcher(text)
		while (matcher.find()) {
			ranges.add(Range(matcher.start(), matcher.end()))
		}
		return ranges
	}

	private fun createUrlListener(context: Context?, uri: Uri): OnSpanClickListener {
		return object : OnSpanClickListener {
			override fun onClick(span: CharSequence) {
				val intent = Intent(Intent.ACTION_VIEW, uri)
				intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
				context?.startActivity(intent)
			}
		}
	}

	private fun makeTagsClickable(view: TextView) {
		view.movementMethod = ClickableMovementMethod
		view.isLongClickable = false
		view.isClickable = false
	}

	private fun isValidPos(pos: Int) = pos >= 0 && text.length > pos
}