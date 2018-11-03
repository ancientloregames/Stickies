package com.ancientlore.stickies.view;

import android.support.annotation.IdRes;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import org.jetbrains.annotations.NotNull;

abstract class SimpleActionModeCallback implements ActionMode.Callback
{
	abstract void manageActionMenu(Menu menu);

	abstract boolean onActionSelected(@IdRes int actionId);

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu)
	{
		manageActionMenu(menu);
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu)
	{
		return true;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, @NotNull MenuItem item)
	{
		return onActionSelected(item.getItemId());
	}

	@Override
	public void onDestroyActionMode(final ActionMode mode)
	{
	}
}
