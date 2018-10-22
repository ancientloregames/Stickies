package com.ancientlore.stickies.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import com.ancientlore.stickies.R;


public class DrawableCompatEditText extends AppCompatEditText
{
	public DrawableCompatEditText(Context context)
	{
		super(context);
	}

	public DrawableCompatEditText(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initAttrs(context, attrs);
	}

	public DrawableCompatEditText(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		initAttrs(context, attrs);
	}

	void initAttrs(Context context, AttributeSet attrs)
	{
		if (attrs != null)
		{
			TypedArray attributeArray = context.obtainStyledAttributes(
					attrs,
					R.styleable.DrawableCompatTextView);

			Drawable drawableStart = null;
			Drawable drawableEnd = null;
			Drawable drawableBottom = null;
			Drawable drawableTop = null;

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
			{
				drawableStart = attributeArray.getDrawable(R.styleable.DrawableCompatTextView_drawableStart);
				drawableEnd = attributeArray.getDrawable(R.styleable.DrawableCompatTextView_drawableEnd);
				drawableBottom = attributeArray.getDrawable(R.styleable.DrawableCompatTextView_drawableBottom);
				drawableTop = attributeArray.getDrawable(R.styleable.DrawableCompatTextView_drawableTop);
			}
			else
			{
				final int drawableStartId = attributeArray.getResourceId(R.styleable.DrawableCompatTextView_drawableStart, -1);
				final int drawableEndId = attributeArray.getResourceId(R.styleable.DrawableCompatTextView_drawableEnd, -1);
				final int drawableBottomId = attributeArray.getResourceId(R.styleable.DrawableCompatTextView_drawableBottom, -1);
				final int drawableTopId = attributeArray.getResourceId(R.styleable.DrawableCompatTextView_drawableTop, -1);

				if (drawableStartId != -1)
					drawableStart = AppCompatResources.getDrawable(context, drawableStartId);
				if (drawableEndId != -1)
					drawableEnd = AppCompatResources.getDrawable(context, drawableEndId);
				if (drawableBottomId != -1)
					drawableBottom = AppCompatResources.getDrawable(context, drawableBottomId);
				if (drawableTopId != -1)
					drawableTop = AppCompatResources.getDrawable(context, drawableTopId);
			}

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
				setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, drawableTop, drawableEnd, drawableBottom);
			else setCompoundDrawablesWithIntrinsicBounds(drawableStart, drawableTop, drawableEnd, drawableBottom);

			attributeArray.recycle();
		}
	}
}
