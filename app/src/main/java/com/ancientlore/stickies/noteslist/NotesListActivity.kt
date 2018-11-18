package com.ancientlore.stickies.noteslist

import android.content.Intent
import android.content.res.Resources
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import android.widget.PopupMenu
import com.ancientlore.stickies.*
import com.ancientlore.stickies.addeditnote.AddEditNoteActivity
import com.ancientlore.stickies.databinding.ActivityNoteslistBinding
import com.ancientlore.stickies.notedetail.NoteDetailActivity
import com.ancientlore.stickies.noteslist.NotesListViewModel.Companion.OPTION_FILTER
import com.ancientlore.stickies.noteslist.NotesListViewModel.Companion.OPTION_SORT
import com.ancientlore.stickies.sortdialog.SortDialogFragment
import com.ancientlore.stickies.topicpicker.TopicPickerActivity
import kotlinx.android.synthetic.main.activity_noteslist.*

class NotesListActivity : BasicActivity<ActivityNoteslistBinding, NotesListViewModel>(), ViewTreeObserver.OnGlobalLayoutListener {

	private val sreenHeight get() = Resources.getSystem().displayMetrics.heightPixels

	override fun onGlobalLayout() {
		val maybeKeyboard = rootContainer.height < sreenHeight * 2 / 3
		onKeyboardStateChanged(maybeKeyboard)
	}

	override fun getLayoutId() = R.layout.activity_noteslist

	override fun getBindingVariable() = BR.viewModel

	override fun createViewModel() = NotesListViewModel(application, getListAdapter())

	override fun onResume() {
		super.onResume()
		rootContainer.viewTreeObserver.addOnGlobalLayoutListener(this)
	}

	override fun onPause() {
		super.onPause()
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			rootContainer.viewTreeObserver.removeOnGlobalLayoutListener(this)
		} else rootContainer.viewTreeObserver.removeGlobalOnLayoutListener(this)
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.notes_list_menu, menu)

		return true
	}

	override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
		R.id.filter -> viewModel.handleOptionSelection(OPTION_FILTER)
		R.id.sort -> viewModel.handleOptionSelection(OPTION_SORT)
		else -> super.onOptionsItemSelected(item)
	}

	override fun setupView(savedInstanceState: Bundle?) {
		super.setupView(savedInstanceState)
		setupActionBar()
		setupList()
	}

	override fun setupViewModel() {
		super.setupViewModel()

		subscriptions.add(viewModel.observeOpenNoteFormRequest()
				.subscribe { startNoteAddition() })

		subscriptions.add(viewModel.observeEditNote()
				.subscribe { startNoteEditing(it) })

		subscriptions.add(viewModel.observeShowNote()
				.subscribe { openNoteDetails(it) })

		subscriptions.add(viewModel.observeShowFilterMenu()
				.subscribe { showFilterMenu() })

		subscriptions.add(viewModel.observeShowSortMenu()
				.subscribe { showSortMenu(it) })

		subscriptions.add(viewModel.observeScrollToTopRequest()
				.subscribe { notesListView.smoothScrollToPosition(0) })

		subscriptions.add(viewModel.observeShowTopicPickerRequest()
				.subscribe { showTopicPicker() })
	}

	private fun setupList() {
		notesListView.adapter = NotesListAdapter(this, mutableListOf())
		notesListView.layoutManager = object: LinearLayoutManager(this) {
			override fun requestChildRectangleOnScreen(parent: RecyclerView, child: View, rect: Rect, immediate: Boolean) = false
			override fun requestChildRectangleOnScreen(parent: RecyclerView, child: View, rect: Rect, immediate: Boolean, focusedChildVisible: Boolean) = false
		}
	}

	private fun getListAdapter() = notesListView.adapter as NotesListAdapter

	private fun showFilterMenu() {
		val popup = PopupMenu(this, findViewById(R.id.filter))
		popup.menuInflater.inflate(R.menu.notes_list_filter_menu, popup.menu)

		popup.setOnMenuItemClickListener { onFilterSelected(it.itemId) }

		popup.show()
	}

	private fun onFilterSelected(itemId: Int): Boolean {
		when (itemId) {
			R.id.all -> viewModel.handleFilterSelection(NotesListViewModel.FILTER_ALL)
			R.id.important -> viewModel.handleFilterSelection(NotesListViewModel.FILTER_IMPORTANT)
			R.id.topic -> viewModel.handleFilterSelection(NotesListViewModel.FILTER_TOPIC)
			else -> return false
		}

		return true
	}

	private fun startNoteAddition() {
		val intent = Intent(this, AddEditNoteActivity::class.java)
		startActivityForResult(intent, NotesListViewModel.INTENT_ADD_NOTE)
	}

	private fun startNoteEditing(id: Long) {
		val intent = Intent(this, AddEditNoteActivity::class.java).apply {
			putExtra(C.EXTRA_NOTE_ID, id)
		}
		startActivityForResult(intent, NotesListViewModel.INTENT_EDIT_NOTE)
	}

	private fun openNoteDetails(id: Long) {
		val intent = Intent(this, NoteDetailActivity::class.java).apply {
			putExtra(C.EXTRA_NOTE_ID, id)
		}
		startActivityForResult(intent, NotesListViewModel.INTENT_SHOW_NOTE)
	}

	private fun showSortMenu(@SortOrder currentSortOrder: String) {
		val dialog = SortDialogFragment.newInstance(currentSortOrder)

		dialog.listener = object : SortDialogFragment.Listener {
			override fun onSortSelected(@SortField field: String, @SortOrder order: String) {
				viewModel.sort(field, order)
			}
		}

		dialog.show(supportFragmentManager, "SortDialog")
	}

	private fun showTopicPicker() {
		val intent = Intent(this, TopicPickerActivity::class.java)
		startActivityForResult(intent, NotesListViewModel.INTENT_SHOW_TOPIC_PICKER)
	}

	private fun onKeyboardStateChanged(opened: Boolean) {
//		switchOpenNoteFormButton(!opened)
		viewModel.onKeyboardStateChanged(opened)
	}

	private fun switchOpenNoteFormButton(show: Boolean) {
		hintFullNote.animate()
				.alpha(if(show) 1f else 0f)
				.translationY(if (show) 0f else -50f)
				.setDuration(100)
				.start()
		openNoteFormButton.animate()
				.alpha(if(show) 1f else 0f)
				.translationY(if (show) 0f else -50f)
				.setDuration(100)
				.start()
	}
}
