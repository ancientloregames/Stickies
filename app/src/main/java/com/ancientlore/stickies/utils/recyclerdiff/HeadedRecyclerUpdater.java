package com.ancientlore.stickies.utils.recyclerdiff;

import android.support.annotation.NonNull;
import android.support.v7.util.ListUpdateCallback;
import com.ancientlore.stickies.HeadedRecyclerAdapter;


public class HeadedRecyclerUpdater implements ListUpdateCallback
{
	@NonNull
	private final HeadedRecyclerAdapter mAdapter;

	/**
	 * Creates an HeadedRecyclerUpdater that will dispatch update events to the given adapter.
	 *
	 * @param adapter The Adapter to send updates to.
	 */
	HeadedRecyclerUpdater(@NonNull HeadedRecyclerAdapter adapter) {
		mAdapter = adapter;
	}

	/** {@inheritDoc} */
	@Override
	public void onInserted(int position, int count) {
		mAdapter.notifyListItemRangeInserted(position, count);
	}

	/** {@inheritDoc} */
	@Override
	public void onRemoved(int position, int count) {
		mAdapter.notifyListItemRangeRemoved(position, count);
	}

	/** {@inheritDoc} */
	@Override
	public void onMoved(int fromPosition, int toPosition) {
		mAdapter.notifyListItemMoved(fromPosition, toPosition);
	}

	/** {@inheritDoc} */
	@Override
	public void onChanged(int position, int count, Object payload) {
		mAdapter.notifyListItemRangeChanged(position, count, payload);
	}
}
