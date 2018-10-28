/*
  The Apache License, Version 2.0
  <p>
  Copyright 2016 Eduard Sergeev
  </p>
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  <p>
  http://www.apache.org/licenses/LICENSE-2.0
  </p>
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package com.ancientlore.stickies.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import com.ancientlore.stickies.R;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

@SuppressWarnings("unused")
public class SwipeLayout extends ViewGroup
{
	private static final String TAG = SwipeLayout.class.getSimpleName();
	private static final float VELOCITY_THRESHOLD = 1500f;

	private ViewDragHelper dragHelper;
	private View leftView;
	private View rightView;
	private View centerView;
	private float velocityThreshold;
	private float touchSlop;
	private OnSwipeListener swipeListener;
	private WeakReference<ObjectAnimator> resetAnimator;
	private final Map<View, Boolean> hackedParents = new WeakHashMap<>();
	private boolean leftSwipeEnabled = true;
	private boolean rightSwipeEnabled = true;

	protected static final int STATE_MENU_HIDDEN = 0;
	protected static final int STATE_MENU_LEFT = 1;
	protected static final int STATE_MENU_RIGHT = 2;

	private int state = STATE_MENU_HIDDEN;

	private static final int TOUCH_STATE_WAIT = 0;
	private static final int TOUCH_STATE_SWIPE = 1;
	private static final int TOUCH_STATE_SKIP = 2;

	private int touchState = TOUCH_STATE_WAIT;
	private float touchX;
	private float touchY;

	public SwipeLayout(Context context)
	{
		super(context);
		init(context, null);
	}

	public SwipeLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context, attrs);
	}

	public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs)
	{
		dragHelper = ViewDragHelper.create(this, 1f, dragCallback);
		velocityThreshold = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, VELOCITY_THRESHOLD, getResources().getDisplayMetrics());
		touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

		if (attrs != null)
		{
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwipeLayout);
			if (a.hasValue(R.styleable.SwipeLayout_swipe_enabled))
			{
				leftSwipeEnabled = a.getBoolean(R.styleable.SwipeLayout_swipe_enabled, true);
				rightSwipeEnabled = a.getBoolean(R.styleable.SwipeLayout_swipe_enabled, true);
			}
			if (a.hasValue(R.styleable.SwipeLayout_left_swipe_enabled))
			{
				leftSwipeEnabled = a.getBoolean(R.styleable.SwipeLayout_left_swipe_enabled, true);
			}
			if (a.hasValue(R.styleable.SwipeLayout_right_swipe_enabled))
			{
				rightSwipeEnabled = a.getBoolean(R.styleable.SwipeLayout_right_swipe_enabled, true);
			}

			a.recycle();
		}
	}

	public void setOnSwipeListener(OnSwipeListener swipeListener)
	{
		this.swipeListener = swipeListener;
	}

	/**
	 * reset swipe-layout state to initial position
	 */
	public void reset()
	{
		if (centerView == null)
		{
			return;
		}

		finishResetAnimator();
		dragHelper.abort();

		offsetChildren(null, -centerView.getLeft());
		state = STATE_MENU_HIDDEN;
	}

	/**
	 * reset swipe-layout state to initial position with animation (200ms)
	 */
	public void animateReset()
	{
		if (centerView == null)
		{
			return;
		}

		finishResetAnimator();
		dragHelper.abort();

		ObjectAnimator animator = new ObjectAnimator();
		animator.setTarget(this);
		animator.setPropertyName("offset");
		animator.setInterpolator(new AccelerateInterpolator());
		animator.setIntValues(centerView.getLeft(), 0);
		animator.setDuration(200);
		animator.start();
		resetAnimator = new WeakReference<>(animator);

		state = STATE_MENU_HIDDEN;
	}

	private void finishResetAnimator()
	{
		if (resetAnimator == null)
		{
			return;
		}

		ObjectAnimator animator = resetAnimator.get();
		if (animator != null)
		{
			resetAnimator.clear();
			if (animator.isRunning())
			{
				animator.end();
			}
		}
	}

	/**
	 * get horizontal offset from initial position
	 */
	public int getOffset()
	{
		return centerView == null ? 0 : centerView.getLeft();
	}

	/**
	 * set horizontal offset from initial position
	 */
	public void setOffset(int offset)
	{
		if (centerView != null)
		{
			offsetChildren(null, offset - centerView.getLeft());
		}
	}

	public boolean isSwipeEnabled()
	{
		return leftSwipeEnabled || rightSwipeEnabled;
	}

	public boolean isLeftSwipeEnabled()
	{
		return leftSwipeEnabled;
	}

	public boolean isRightSwipeEnabled()
	{
		return rightSwipeEnabled;
	}

	/**
	 * enable or disable swipe gesture handling
	 */
	public void setSwipeEnabled(boolean enabled)
	{
		this.leftSwipeEnabled = enabled;
		this.rightSwipeEnabled = enabled;
	}

	/**
	 * Enable or disable swipe gesture from left side
	 */
	public void setLeftSwipeEnabled(boolean leftSwipeEnabled)
	{
		this.leftSwipeEnabled = leftSwipeEnabled;
	}

	/**
	 * Enable or disable swipe gesture from right side
	 */

	public void setRightSwipeEnabled(boolean rightSwipeEnabled)
	{
		this.rightSwipeEnabled = rightSwipeEnabled;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int count = getChildCount();

		int maxHeight = 0;

		// Find out how big everyone wants to be
		if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY)
		{
			measureChildren(widthMeasureSpec, heightMeasureSpec);
		}
		else
		{
			//find a child with biggest height
			for (int i = 0; i < count; i++)
			{
				View child = getChildAt(i);
				measureChild(child, widthMeasureSpec, heightMeasureSpec);
				maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
			}

			if (maxHeight > 0)
			{
				heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.EXACTLY);
				measureChildren(widthMeasureSpec, heightMeasureSpec);
			}
		}

		// Find rightmost and bottom-most child
		for (int i = 0; i < count; i++)
		{
			View child = getChildAt(i);
			if (child.getVisibility() != GONE)
			{
				int childBottom;

				childBottom = child.getMeasuredHeight();
				maxHeight = Math.max(maxHeight, childBottom);
			}
		}

		maxHeight += getPaddingTop() + getPaddingBottom();
		maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());

		setMeasuredDimension(resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec), maxHeight);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom)
	{
		layoutChildren(left, top, right, bottom);
	}

	private void layoutChildren(int left, int top, int right, int bottom)
	{
		final int count = getChildCount();

		final int parentTop = getPaddingTop();

		centerView = null;
		leftView = null;
		rightView = null;
		for (int i = 0; i < count; i++)
		{
			View child = getChildAt(i);
			if (child.getVisibility() == GONE)
			{
				continue;
			}

			LayoutParams lp = (LayoutParams) child.getLayoutParams();
			switch (lp.gravity)
			{
				case LayoutParams.CENTER:
					centerView = child;
					break;

				case LayoutParams.LEFT:
					leftView = child;
					break;

				case LayoutParams.RIGHT:
					rightView = child;
					break;
			}
		}

		if (centerView == null)
		{
			throw new RuntimeException("Center view must be added");
		}

		for (int i = 0; i < count; i++)
		{
			final View child = getChildAt(i);
			if (child.getVisibility() != GONE)
			{
				final LayoutParams lp = (LayoutParams) child.getLayoutParams();

				final int width = child.getMeasuredWidth();
				final int height = child.getMeasuredHeight();

				int childLeft;
				int childTop;

				int orientation = lp.gravity;

				switch (orientation)
				{
					case LayoutParams.LEFT:
						childLeft = centerView.getLeft() - width;
						break;

					case LayoutParams.RIGHT:
						childLeft = centerView.getRight();
						break;

					case LayoutParams.CENTER:
					default:
						childLeft = child.getLeft();
						break;
				}
				childTop = parentTop;

				child.layout(childLeft, childTop, childLeft + width, childTop + height);
			}
		}
	}

	private final ViewDragHelper.Callback dragCallback = new ViewDragHelper.Callback()
	{

		private int initLeft;

		@Override
		public boolean tryCaptureView(@NotNull View child, int pointerId)
		{
			initLeft = child.getLeft();
			return true;
		}

		@Override
		public int clampViewPositionHorizontal(@NotNull View child, int left, int dx)
		{
			if (dx > 0)
			{
				return clampMoveRight(child, left);
			}
			else
			{
				return clampMoveLeft(child, left);
			}
		}

		@Override
		public int getViewHorizontalDragRange(@NotNull View child)
		{
			return getWidth();
		}

		@Override
		public void onViewReleased(@NotNull View releasedChild, float xvel, float yvel)
		{
			Log.d(TAG, "VELOCITY " + xvel + "; THRESHOLD " + velocityThreshold);

			int dx = releasedChild.getLeft() - initLeft;
			if (dx == 0)
			{
				return;
			}

			boolean handled = dx > 0
					? xvel >= 0 ? onMoveRightReleased(releasedChild, dx, xvel) : onMoveLeftReleased(releasedChild, dx, xvel)
					: xvel <= 0 ? onMoveLeftReleased(releasedChild, dx, xvel) : onMoveRightReleased(releasedChild, dx, xvel);

			if (!handled)
			{
				startScrollAnimation(releasedChild, releasedChild.getLeft() - centerView.getLeft(), false, dx > 0);
			}
		}

		private boolean leftViewClampReached(@NotNull LayoutParams leftViewLP)
		{
			if (leftView == null)
			{
				return false;
			}

			switch (leftViewLP.clamp)
			{
				case LayoutParams.CLAMP_PARENT:
					return leftView.getRight() >= getWidth();

				case LayoutParams.CLAMP_SELF:
					return leftView.getRight() >= leftView.getWidth();

				default:
					return leftView.getRight() >= leftViewLP.clamp;
			}
		}

		private boolean rightViewClampReached(@NotNull LayoutParams lp)
		{
			if (rightView == null)
			{
				return false;
			}

			switch (lp.clamp)
			{
				case LayoutParams.CLAMP_PARENT:
					return rightView.getRight() <= getWidth();

				case LayoutParams.CLAMP_SELF:
					return rightView.getRight() <= getWidth();

				default:
					return rightView.getLeft() + lp.clamp <= getWidth();
			}
		}

		@Override
		public void onViewPositionChanged(@NotNull View changedView, int left, int top, int dx, int dy)
		{
			offsetChildren(changedView, dx);

			if (swipeListener == null)
			{
				return;
			}

			int stickyBound;
			if (dx > 0)
			{
				//move to right

				if (leftView != null)
				{
					stickyBound = getStickyBound(leftView);
					if (stickyBound != LayoutParams.STICKY_NONE)
					{
						if (leftView.getRight() - stickyBound > 0 && leftView.getRight() - stickyBound - dx <= 0)
						{
							swipeListener.onLeftStickyEdge(SwipeLayout.this, true);
						}
					}
				}

				if (rightView != null)
				{
					stickyBound = getStickyBound(rightView);
					if (stickyBound != LayoutParams.STICKY_NONE)
					{
						if (rightView.getLeft() + stickyBound > getWidth() && rightView.getLeft() + stickyBound - dx <= getWidth())
						{
							swipeListener.onRightStickyEdge(SwipeLayout.this, true);
						}
					}
				}
			}
			else if (dx < 0)
			{
				//move to left

				if (leftView != null)
				{
					stickyBound = getStickyBound(leftView);
					if (stickyBound != LayoutParams.STICKY_NONE)
					{
						if (leftView.getRight() - stickyBound <= 0 && leftView.getRight() - stickyBound - dx > 0)
						{
							swipeListener.onLeftStickyEdge(SwipeLayout.this, false);
						}
					}
				}

				if (rightView != null)
				{
					stickyBound = getStickyBound(rightView);
					if (stickyBound != LayoutParams.STICKY_NONE)
					{
						if (rightView.getLeft() + stickyBound <= getWidth() && rightView.getLeft() + stickyBound - dx > getWidth())
						{
							swipeListener.onRightStickyEdge(SwipeLayout.this, false);
						}
					}
				}
			}
		}

		private int getStickyBound(View view)
		{
			LayoutParams lp = getLayoutParams(view);
			if (lp.sticky == LayoutParams.STICKY_NONE)
			{
				return LayoutParams.STICKY_NONE;
			}

			return lp.sticky == LayoutParams.STICKY_SELF ? view.getWidth() : lp.sticky;
		}

		private int clampMoveRight(View child, int left)
		{
			if (leftView == null)
			{
				return child == centerView ? Math.min(left, 0) : Math.min(left, getWidth());
			}

			LayoutParams lp = getLayoutParams(leftView);
			switch (lp.clamp)
			{
				case LayoutParams.CLAMP_PARENT:
					return Math.min(left, getWidth() + child.getLeft() - leftView.getRight());

				case LayoutParams.CLAMP_SELF:
					return Math.min(left, child.getLeft() - leftView.getLeft());

				default:
					return Math.min(left, child.getLeft() - leftView.getRight() + lp.clamp);
			}
		}

		private int clampMoveLeft(View child, int left)
		{
			if (rightView == null)
			{
				return child == centerView ? Math.max(left, 0) : Math.max(left, -child.getWidth());
			}

			LayoutParams lp = getLayoutParams(rightView);
			switch (lp.clamp)
			{
				case LayoutParams.CLAMP_PARENT:
					return Math.max(child.getLeft() - rightView.getLeft(), left);

				case LayoutParams.CLAMP_SELF:
					return Math.max(left, getWidth() - rightView.getLeft() + child.getLeft() - rightView.getWidth());

				default:
					return Math.max(left, getWidth() - rightView.getLeft() + child.getLeft() - lp.clamp);
			}
		}

		private boolean onMoveRightReleased(View child, int dx, float xvel)
		{

			if (xvel > velocityThreshold)
			{
				int left = centerView.getLeft() < 0 ? child.getLeft() - centerView.getLeft() : getWidth();
				boolean moveToOriginal = centerView.getLeft() < 0;
				startScrollAnimation(child, clampMoveRight(child, left), !moveToOriginal, true);
				return true;
			}

			if (leftView == null)
			{
				startScrollAnimation(child, child.getLeft() - centerView.getLeft(), false, true);
				return true;
			}

			LayoutParams lp = getLayoutParams(leftView);

			if (dx > 0 && xvel >= 0 && leftViewClampReached(lp))
			{
				if (swipeListener != null)
				{
					swipeListener.onSwipeClampReached(SwipeLayout.this, true);
				}
				return true;
			}

			if (dx > 0 && xvel >= 0 && lp.bringToClamp != LayoutParams.BRING_TO_CLAMP_NO && leftView.getRight() > lp.bringToClamp)
			{
				int left = centerView.getLeft() < 0 ? child.getLeft() - centerView.getLeft() : getWidth();
				startScrollAnimation(child, clampMoveRight(child, left), true, true);
				return true;
			}

			if (lp.sticky != LayoutParams.STICKY_NONE)
			{
				int stickyBound = lp.sticky == LayoutParams.STICKY_SELF ? leftView.getWidth() : lp.sticky;
				float amplitude = stickyBound * lp.stickySensitivity;

				if (isBetween(-amplitude, amplitude, centerView.getLeft() - stickyBound))
				{
					boolean toClamp = (lp.clamp == LayoutParams.CLAMP_SELF && stickyBound == leftView.getWidth()) ||
							lp.clamp == stickyBound ||
							(lp.clamp == LayoutParams.CLAMP_PARENT && stickyBound == getWidth());
					startScrollAnimation(child, child.getLeft() - centerView.getLeft() + stickyBound, toClamp, true);
					return true;
				}
			}
			return false;
		}

		private boolean onMoveLeftReleased(View child, int dx, float xvel)
		{
			if (-xvel > velocityThreshold)
			{
				int left = centerView.getLeft() > 0 ? child.getLeft() - centerView.getLeft() : -getWidth();
				boolean moveToOriginal = centerView.getLeft() > 0;
				startScrollAnimation(child, clampMoveLeft(child, left), !moveToOriginal, false);
				return true;
			}

			if (rightView == null)
			{
				startScrollAnimation(child, child.getLeft() - centerView.getLeft(), false, false);
				return true;
			}

			LayoutParams lp = getLayoutParams(rightView);

			if (dx < 0 && xvel <= 0 && rightViewClampReached(lp))
			{
				if (swipeListener != null)
				{
					swipeListener.onSwipeClampReached(SwipeLayout.this, false);
				}
				return true;
			}

			if (dx < 0 && xvel <= 0 && lp.bringToClamp != LayoutParams.BRING_TO_CLAMP_NO && rightView.getLeft() + lp.bringToClamp < getWidth())
			{
				int left = centerView.getLeft() > 0 ? child.getLeft() - centerView.getLeft() : -getWidth();
				startScrollAnimation(child, clampMoveLeft(child, left), true, false);
				return true;
			}

			if (lp.sticky != LayoutParams.STICKY_NONE)
			{
				int stickyBound = lp.sticky == LayoutParams.STICKY_SELF ? rightView.getWidth() : lp.sticky;
				float amplitude = stickyBound * lp.stickySensitivity;

				if (isBetween(-amplitude, amplitude, centerView.getRight() + stickyBound - getWidth()))
				{
					boolean toClamp = (lp.clamp == LayoutParams.CLAMP_SELF && stickyBound == rightView.getWidth()) ||
							lp.clamp == stickyBound ||
							(lp.clamp == LayoutParams.CLAMP_PARENT && stickyBound == getWidth());
					startScrollAnimation(child, child.getLeft() - rightView.getLeft() + getWidth() - stickyBound, toClamp, false);
					return true;
				}
			}

			return false;
		}

		@Contract(pure = true)
		private boolean isBetween(float left, float right, float check)
		{
			return check >= left && check <= right;
		}
	};

	private void startScrollAnimation(@NotNull View view, int targetX, boolean moveToClamp, boolean toRight)
	{
		state = getState(moveToClamp, toRight);

		if (dragHelper.settleCapturedViewAt(targetX, view.getTop()))
		{
			ViewCompat.postOnAnimation(view, new SettleRunnable(view, moveToClamp, toRight));
		}
		else if (moveToClamp && swipeListener != null)
		{
			swipeListener.onSwipeClampReached(SwipeLayout.this, toRight);
		}
	}

	private LayoutParams getLayoutParams(@NotNull View view)
	{
		return (LayoutParams) view.getLayoutParams();
	}

	private void offsetChildren(View skip, int dx)
	{
		if (dx == 0)
		{
			return;
		}

		int count = getChildCount();
		for (int i = 0; i < count; i++)
		{
			View child = getChildAt(i);
			if (child == skip)
			{
				continue;
			}

			child.offsetLeftAndRight(dx);
			invalidate();
		}
	}

	private void hackParents()
	{
		ViewParent parent = getParent();
		while (parent != null)
		{
			if (parent instanceof NestedScrollingParent)
			{
				View view = (View) parent;
				hackedParents.put(view, view.isEnabled());
			}
			parent = parent.getParent();
		}
	}

	private void unHackParents()
	{
		for (Map.Entry<View, Boolean> entry : hackedParents.entrySet())
		{
			View view = entry.getKey();
			if (view != null)
			{
				view.setEnabled(entry.getValue());
			}
		}
		hackedParents.clear();
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event)
	{
		return onTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (!isSwipeEnabled()) return super.onTouchEvent(event);

		switch (event.getActionMasked())
		{
			case MotionEvent.ACTION_DOWN:
				touchState = TOUCH_STATE_WAIT;
				touchX = event.getX();
				touchY = event.getY();
				break;

			case MotionEvent.ACTION_MOVE:
				if (touchState == TOUCH_STATE_WAIT)
				{
					float dx = Math.abs(event.getX() - touchX);
					float dy = Math.abs(event.getY() - touchY);

					boolean isLeftToRight = (event.getX() - touchX) > 0;

					if (((isLeftToRight && !leftSwipeEnabled) || (!isLeftToRight && !rightSwipeEnabled))
							&&
							getOffset() == 0)
					{

						return super.onTouchEvent(event);
					}

					if (dx >= touchSlop || dy >= touchSlop)
					{
						touchState = dx / 3 > dy ? TOUCH_STATE_SWIPE : TOUCH_STATE_SKIP;
						if (touchState == TOUCH_STATE_SWIPE)
						{
							requestDisallowInterceptTouchEvent(true);

							hackParents();

							if (swipeListener != null)
							{
								swipeListener.onBeginSwipe(this, event.getX() > touchX);
							}
						}
					}
				}
				break;

			case MotionEvent.ACTION_CANCEL:
				if (touchState != TOUCH_STATE_SWIPE && swipeListener != null)
				{
					swipeListener.onNotSwipe(this);
				}
				touchState = TOUCH_STATE_WAIT;
				break;

			case MotionEvent.ACTION_POINTER_UP:
			case MotionEvent.ACTION_UP:
				if (touchState == TOUCH_STATE_SWIPE)
				{
					unHackParents();
					requestDisallowInterceptTouchEvent(false);
				}
				touchState = TOUCH_STATE_WAIT;
				break;
		}

		if (event.getActionMasked() != MotionEvent.ACTION_MOVE || touchState == TOUCH_STATE_SWIPE)
		{
			dragHelper.processTouchEvent(event);
		}

		return touchState == TOUCH_STATE_SWIPE;
	}

	@Override
	protected LayoutParams generateDefaultLayoutParams()
	{
		return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	}

	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs)
	{
		return new LayoutParams(getContext(), attrs);
	}

	@Override
	protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p)
	{
		return new LayoutParams(p);
	}

	@Override
	protected boolean checkLayoutParams(ViewGroup.LayoutParams p)
	{
		return p instanceof LayoutParams;
	}

	@Contract(pure = true)
	private int getState(boolean moveToClamp, boolean toRight)
	{
		if (moveToClamp && !toRight)
			return STATE_MENU_RIGHT;
		else if (moveToClamp)
			return STATE_MENU_LEFT;
		else return STATE_MENU_HIDDEN;
	}

	public boolean isMenuOpened()
	{
		return state != STATE_MENU_HIDDEN;
	}

	private class SettleRunnable implements Runnable
	{
		private final View view;
		private final boolean moveToClamp;
		private final boolean moveToRight;

		SettleRunnable(View view, boolean moveToClamp, boolean moveToRight)
		{
			this.view = view;
			this.moveToClamp = moveToClamp;
			this.moveToRight = moveToRight;
		}

		public void run()
		{
			if (dragHelper != null && dragHelper.continueSettling(true))
			{
				ViewCompat.postOnAnimation(this.view, this);
			}
			else
			{
				Log.d(TAG, "ONSWIPE clamp: " + moveToClamp + " ; moveToRight: " + moveToRight);
				if (moveToClamp && swipeListener != null)
				{
					swipeListener.onSwipeClampReached(SwipeLayout.this, moveToRight);
				}
			}
		}
	}

	@SuppressWarnings("WeakerAccess")
	public static class LayoutParams extends ViewGroup.LayoutParams
	{
		public static final int LEFT = -1;
		public static final int RIGHT = 1;
		public static final int CENTER = 0;

		public static final int CLAMP_PARENT = -1;
		public static final int CLAMP_SELF = -2;
		public static final int BRING_TO_CLAMP_NO = -1;

		public static final int STICKY_SELF = -1;
		public static final int STICKY_NONE = -2;
		private static final float DEFAULT_STICKY_SENSITIVITY = 0.9f;

		private int gravity = CENTER;
		private int sticky;
		private float stickySensitivity = DEFAULT_STICKY_SENSITIVITY;
		private int clamp = CLAMP_SELF;
		private int bringToClamp = BRING_TO_CLAMP_NO;

		public LayoutParams(Context c, AttributeSet attrs)
		{
			super(c, attrs);

			TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.SwipeLayout);

			final int N = a.getIndexCount();
			for (int i = 0; i < N; ++i)
			{
				int attr = a.getIndex(i);
				if (attr == R.styleable.SwipeLayout_gravity)
				{
					gravity = a.getInt(attr, CENTER);
				}
				else if (attr == R.styleable.SwipeLayout_sticky)
				{
					sticky = a.getLayoutDimension(attr, STICKY_SELF);
				}
				else if (attr == R.styleable.SwipeLayout_clamp)
				{
					clamp = a.getLayoutDimension(attr, CLAMP_SELF);
				}
				else if (attr == R.styleable.SwipeLayout_bring_to_clamp)
				{
					bringToClamp = a.getLayoutDimension(attr, BRING_TO_CLAMP_NO);
				}
				else if (attr == R.styleable.SwipeLayout_sticky_sensitivity)
				{
					stickySensitivity = a.getFloat(attr, DEFAULT_STICKY_SENSITIVITY);
				}
			}
			a.recycle();
		}

		public LayoutParams(ViewGroup.LayoutParams source)
		{
			super(source);
		}

		public LayoutParams(int width, int height)
		{
			super(width, height);
		}
	}

	public interface OnSwipeListener
	{
		void onBeginSwipe(SwipeLayout swipeLayout, boolean moveToRight);

		void onSwipeClampReached(SwipeLayout swipeLayout, boolean moveToRight);

		void onLeftStickyEdge(SwipeLayout swipeLayout, boolean moveToRight);

		void onRightStickyEdge(SwipeLayout swipeLayout, boolean moveToRight);

		void onNotSwipe(SwipeLayout swipeLayout);
	}
}