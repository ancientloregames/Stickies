package com.ancientlore.stickies.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

public class FlickableLayout extends ViewGroup
{
	interface Listener
	{
		void onFlicked();
	}

	private static final float DISPLAY_DENSITY = Resources.getSystem().getDisplayMetrics().density;
	private static final int LONGCLICK_TIMEOUT = ViewConfiguration.getLongPressTimeout();

	private View contentView;

	public boolean swipeBackEnabled = true;

	public float contentTranslationX;
	private int startX;
	private int startY;
	private boolean pressing;
	private boolean dragging;
	private boolean animationInProgress;
	private boolean keyboardHidden;
	private VelocityTracker velocityTracker;

	private long longClickTimer;
	private boolean isLongClick;

	private Listener listener;

	public FlickableLayout(@NonNull Context context)
	{
		super(context);
		init(context, null);
	}

	public FlickableLayout(@NonNull Context context, @Nullable AttributeSet attrs)
	{
		super(context, attrs);
		init(context, attrs);
	}

	public FlickableLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public FlickableLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context, attrs);
	}

	private void init(@NonNull Context context, @Nullable AttributeSet attrs)
	{
		setWillNotDraw(false);
	}

	@Override
	protected void onFinishInflate()
	{
		super.onFinishInflate();

		if (getChildCount() > 1)
			throw new IllegalStateException("FlickableLayout can host only one direct child");

		setContentView(getChildAt(0));
	}

	@Override
	public void addView(View child)
	{
		checkChildCount();
		setContentView(child);
		super.addView(child);
	}

	@Override
	public void addView(View child, int index)
	{
		checkChildCount();
		setContentView(child);
		super.addView(child, index);
	}

	@Override
	public void addView(View child, ViewGroup.LayoutParams params)
	{
		checkChildCount();
		setContentView(child);
		super.addView(child, params);
	}

	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams params)
	{
		checkChildCount();
		setContentView(child);
		super.addView(child, index, params);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		measureChildren(widthMeasureSpec, heightMeasureSpec);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom)
	{
		final View child = getChildAt(0);

		if (child.getVisibility() != GONE)
		{
			child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
		}
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		float opacity = Math.min(0.8f, (getWidth() - contentTranslationX) / (float) getWidth());
		if (opacity < 0) opacity = 0;

		Paint paint = new Paint();
		paint.setColor(Color.argb((int)(200 * opacity),0,0,0));
		canvas.drawRect(0, 0, contentTranslationX, getHeight(), paint);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
		return onTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		if (!swipeBackEnabled || animationInProgress) return false;

		switch (ev.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				if (pressing || dragging) break;

				longClickTimer = System.currentTimeMillis();
				isLongClick = false;

				pressing = true;

				startX = (int) ev.getX();
				startY = (int) ev.getY();

				if (velocityTracker != null) velocityTracker.clear();
				break;

			case MotionEvent.ACTION_MOVE:
				if (isLongClick) break;

				if (velocityTracker == null) velocityTracker = VelocityTracker.obtain();
				velocityTracker.addMovement(ev);

				int dx = Math.max(0, (int) (ev.getX() - startX));
				int dy = Math.abs((int) ev.getY() - startY);

				if (pressing && !dragging && dx / DISPLAY_DENSITY >= 30 && dx / 3 > dy)
				{
					pressing = false;
					dragging = true;
					startX = (int) ev.getX();
					keyboardHidden = false;
				}
				else if (dragging)
				{
					hideKeyboard();

					if (dx > 0) setContentTranslationX(dx);
				}
				else if (System.currentTimeMillis() - longClickTimer > LONGCLICK_TIMEOUT)
				{
					pressing = false;
					dragging = false;
					isLongClick = true;
				}
				break;

			case MotionEvent.ACTION_POINTER_UP:
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				if (isLongClick) break;

				if (dragging)
				{
					if (velocityTracker == null) velocityTracker = VelocityTracker.obtain();
					velocityTracker.computeCurrentVelocity(1000);

					float x = contentView.getX();
					boolean swiped = x >= getMeasuredWidth() / 3.0f || velocityTracker.getXVelocity() >= 3500;
					float distance = swiped ? getMeasuredWidth() - x : x;
					float endPosition = swiped ? getMeasuredWidth() : 0;

					AnimatorSet animatorSet = new AnimatorSet();
					animatorSet.play(ObjectAnimator.ofFloat(this, "contentTranslationX", x, endPosition));
					animatorSet.setDuration(Math.max((int) (200.0f / contentView.getMeasuredWidth() * distance), 50));
					animatorSet.addListener(new AnimatorListenerAdapter() {
						@Override public void onAnimationEnd(Animator animator) {
							onSlideAnimationEnd(swiped);
						}
					});
					animatorSet.start();
					animationInProgress = true;
				}

				hideKeyboard();
				resetDragProcessing();
				break;
		}

		return dragging;
	}

	public void setContentTranslationX(float value)
	{
		contentTranslationX = value;
		contentView.setTranslationX(value);
		invalidate();
	}

	public void setListener(Listener listener)
	{
		this.listener = listener;
	}

	private void onSlideAnimationEnd(final boolean swiped)
	{
		if (swiped && listener != null) listener.onFlicked();

		resetDragProcessing();

		animationInProgress = false;
	}

	private void resetDragProcessing()
	{
		pressing = false;
		dragging = false;
		if (velocityTracker != null)
		{
			velocityTracker.recycle();
			velocityTracker = null;
		}
	}

	private void hideKeyboard()
	{
		if (!keyboardHidden)
		{
			((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(getWindowToken(), 0);
			keyboardHidden = true;
		}
	}

	private void checkChildCount()
	{
		if (getChildCount() > 0)
			throw new IllegalStateException("FlickableLayout can host only one direct child");
	}

	private void setContentView(View view)
	{
		contentView = view;
	}
}
