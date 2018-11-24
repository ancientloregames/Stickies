package com.ancientlore.stickies.utils

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.constraint.ConstraintLayout
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.AppCompatAutoCompleteTextView
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.CardView
import com.ancientlore.stickies.menu.topic.TopicsListAdapter
import com.ancientlore.stickies.view.DrawableCompatTextView
import com.ancientlore.stickies.view.StyleableEditText

@BindingAdapter("srcCompat")
fun setImageResource(imageView: AppCompatImageView, res: Int) {
	imageView.setImageResource(res)
}

@BindingAdapter("srcCompat")
fun setImageResource(fab: FloatingActionButton, drawable: Drawable) {
	fab.setImageDrawable(drawable)
}

@BindingAdapter("backgroundTint")
fun setBackgroundTintCompat(view: ConstraintLayout, @ColorInt color: Int) = ViewUtils.setBackgroundTint(view, color)

@BindingAdapter("backgroundTint")
fun setBackgroundTintCompat(view: DrawableCompatTextView, color: Int) = view.setBackgroundTint(color)

@BindingAdapter("strikeThrough")
fun setStrikeThrough(textView: DrawableCompatTextView, strikeThrough: Boolean) = textView.setStrikeThrough(strikeThrough)

@BindingAdapter("drawableStart")
fun setDrawableStart(imageView: DrawableCompatTextView, drawable: Drawable) = imageView.setDrawableStart(drawable)
@BindingAdapter("drawableTop")
fun setDrawableTop(imageView: DrawableCompatTextView, drawable: Drawable) = imageView.setDrawableTop(drawable)
@BindingAdapter("drawableEnd")
fun setDrawableEnd(imageView: DrawableCompatTextView, drawable: Drawable) = imageView.setDrawableEnd(drawable)
@BindingAdapter("drawableBottom")
fun setDrawableBottom(imageView: DrawableCompatTextView, drawable: Drawable) = imageView.setDrawableBottom(drawable)

@InverseBindingAdapter(attribute = "android:text", event = "android:textAttrChanged")
fun getTextString(view: StyleableEditText) = view.text

@BindingAdapter("android:adapter")
fun setAdapter(view: AppCompatAutoCompleteTextView, adapter: TopicsListAdapter) {
	view.setAdapter(adapter)
}