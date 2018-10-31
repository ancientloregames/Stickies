package com.ancientlore.stickies.utils

import android.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.CardView
import com.ancientlore.stickies.view.DrawableCompatTextView

@BindingAdapter("srcCompat")
fun setImageResource(imageView: AppCompatImageView, res: Int) {
	imageView.setImageResource(res)
}

@BindingAdapter("srcCompat")
fun setImageResource(fab: FloatingActionButton, drawable: Drawable) {
	fab.setImageDrawable(drawable)
}

@BindingAdapter("cardBackgroundColor")
fun setBackgroundColor(imageView: CardView, color: Int) {
	imageView.setCardBackgroundColor(color)
}

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