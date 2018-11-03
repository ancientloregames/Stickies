package com.ancientlore.stickies.view;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ancientlore.stickies.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StyleableEditText extends AppCompatEditText
{
	public StyleableEditText(Context context)
	{
		super(context);
		init(context, null);
	}

	public StyleableEditText(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context, attrs);
	}

	public StyleableEditText(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	private void init(@NotNull Context context, @Nullable AttributeSet attrs)
	{
		initTextStylesMenu(context);
	}

	private void initTextStylesMenu(@NotNull Context context)
	{
		final ActionMode.Callback actionModeCallback = new ActionMode.Callback()
		{
			@Override public boolean onCreateActionMode(ActionMode mode, Menu menu)
			{
				new MenuInflater(context).inflate(R.menu.text_styles_menu, menu);
				return true;
			}
			@Override public boolean onPrepareActionMode(ActionMode mode, Menu menu)
			{
				return true;
			}
			@Override public boolean onActionItemClicked(ActionMode mode, MenuItem item)
			{
				return false;
			}
			@Override public void onDestroyActionMode(final ActionMode mode)
			{
			}
		};

		setCustomSelectionActionModeCallback(actionModeCallback);
	}

	private void applyStyle(@IdRes int styleId)
	{
		switch (styleId)
		{
			case R.id.bold:
				break;
			case R.id.italic:
				break;
			case R.id.underlined:
				break;
			case R.id.strikethrough:
				break;
		}
	}
}
