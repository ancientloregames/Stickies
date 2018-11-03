package com.ancientlore.styledstring

import android.text.SpannableStringBuilder

fun SpannableStringBuilder.toStyledString() = StyledString(toString())