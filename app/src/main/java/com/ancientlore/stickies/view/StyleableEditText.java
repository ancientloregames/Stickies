package com.ancientlore.stickies.view;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;

import com.ancientlore.stickies.R;
import com.ancientlore.styledstring.StyledString;

import org.jetbrains.annotations.Contract;
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

	@NotNull
	@Override
	public Editable getText()
	{
		if (super.getText() == null) setText("");
		return super.getText();
	}

	private void init(@NotNull Context context, @Nullable AttributeSet attrs)
	{
		initTextStylesMenu(context);
	}

	private void initTextStylesMenu(@NotNull Context context)
	{
		setCustomSelectionActionModeCallback(createTextStylesCallback(context));
	}

	@Contract(pure = true)
	private boolean handleTextMenuOption(@IdRes int optionId)
	{
		int selectionEnd = getSelectionEnd();
		StyledString text = new StyledString(getText()).forRange(getSelectionStart(), selectionEnd);
		switch (optionId) {
			case R.id.bold:
				text.makeBold();
				break;
			case R.id.italic:
				text.makeItalic();
				break;
			case R.id.underlined:
				text.makeUnderlined();
				break;
			case R.id.strikethrough:
				text.crossOut();
				break;
			default:
				return false;
		}
		setText(text);
		setSelection(selectionEnd);
		return true;
	}

	@NonNull
	@Contract(pure = true)
	private ActionMode.Callback createTextStylesCallback(@NotNull Context context)
	{
		return new TextStylesMenuCallback(context);
	}

	private class TextStylesMenuCallback extends SimpleActionModeCallback
	{
		private final MenuInflater menuInflater;

		private TextStylesMenuCallback(@NotNull Context context)
		{
			menuInflater = new MenuInflater(context);
		}

		@Override
		void manageActionMenu(Menu menu)
		{
			menuInflater.inflate(R.menu.text_styles_menu, menu);
		}

		@Override
		boolean onActionSelected(int actionId)
		{
			return handleTextMenuOption(actionId);
		}
	}

}
