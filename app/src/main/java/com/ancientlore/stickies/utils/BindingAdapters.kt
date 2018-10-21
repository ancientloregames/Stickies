package com.ancientlore.stickies.utils

import android.databinding.BindingAdapter
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.CardView

@BindingAdapter("srcCompat")
fun setImageResource(imageView: AppCompatImageView, res: Int) {
	imageView.setImageResource(res)
}

@BindingAdapter("cardBackgroundColor")
fun setBackgroundColor(imageView: CardView, res: Int) {
	imageView.setCardBackgroundColor(res)
}