package com.ancientlore.stickies.utils

import android.databinding.BindingAdapter
import android.support.v7.widget.AppCompatImageView

@BindingAdapter("srcCompat")
fun setImageResource(imageView: AppCompatImageView, res: Int) {
	imageView.setImageResource(res)
}