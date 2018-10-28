package com.ancientlore.stickies.noteslist

import android.content.Context
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.support.annotation.UiThread
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import com.ancientlore.stickies.*
import com.ancientlore.stickies.data.model.Note
import com.ancientlore.stickies.databinding.NotesListItemBinding
import com.ancientlore.stickies.utils.getListTitle
import com.ancientlore.stickies.utils.hideKeyboard
import com.ancientlore.stickies.utils.recyclerdiff.HeadedRecyclerDiffUtil
import com.ancientlore.stickies.utils.showKeybouard
import java.lang.ref.WeakReference
import java.text.DateFormat

class NotesListAdapter(context: Context, items: MutableList<Note>)
	: BasicRecyclerAdapter<Note, NotesListAdapter.ViewHolder, NotesListItemBinding>(context, items, true, true) {
	interface Listener {
		fun onItemClicked(note: Note)
		fun onNewNote(note: Note)
		fun onImportantButtonClicked(id: Long, newState: Boolean)
		fun onCompletedButtonClicked(id: Long, newState: Boolean)
		fun onDeleteButtonClicked(id: Long)
	}

	data class HeaderParams(var requestFocus: Boolean = false)

	private val timeComparator = Comparator<Note> { o1, o2 -> o1.timeCreated.compareTo(o2.timeCreated) }
	private val titleComparator = Comparator<Note> { o1, o2 -> o1.compareByText(o2) }

	private val headerParams = HeaderParams()

	private var headerTextFieldRef: WeakReference<HeaderViewHolder>? = null

	private var listener: Listener? = null

	override fun createHeaderViewHolder(parent: ViewGroup) =
			HeaderViewHolder(layoutInflater.inflate(R.layout.notes_list_header, parent, false))

	override fun createFooterViewHolder(parent: ViewGroup) =
			FooterViewHolder(layoutInflater.inflate(R.layout.notes_list_footer, parent, false))

	override fun bindHeaderViewHolder(holder: RecyclerView.ViewHolder) {
		val headerHolder = holder as HeaderViewHolder
		headerHolder.bind(headerParams)
		headerParams.requestFocus = false
		headerTextFieldRef = WeakReference(headerHolder)
		headerHolder.listener = object : HeaderViewHolder.Listener {
			override fun onTextSubmited(text: String) = addNoteWithin(text)
		}
	}

	override fun onBindViewHolderInner(holder: ViewHolder, position: Int) {
		super.onBindViewHolderInner(holder, position)

		val note = items[position]

		holder.listener = object : ViewHolder.Listener {
			override fun onImportantButtonClicked() { listener?.onImportantButtonClicked(note.id, !note.isImportant) }
			override fun onCompletedButtonClicked() { listener?.onCompletedButtonClicked(note.id, !note.isCompleted) }
			override fun onDeleteButtonClicked() { listener?.onDeleteButtonClicked(note.id) }
			override fun onItemClicked() { listener?.onItemClicked(note) }
		}
	}

	override fun createItemViewDataBinding(parent: ViewGroup): NotesListItemBinding =
			NotesListItemBinding.inflate(layoutInflater, parent, false)

	override fun getViewHolder(binding: NotesListItemBinding) = ViewHolder(binding)

	override fun getDiffCallback(newItems: List<Note>) = DiffCallback(items, newItems)

	@UiThread
	override fun deleteItem(id: Long) = deleteItemAt(findPosition(id))

	override fun findItem(id: Long) = items.find { it.id == id }

	override fun findPosition(id: Long) = items.indexOfFirst { it.id == id }

	override fun isTheSame(first: Note, second: Note) = first.id == second.id

	override fun isUnique(item: Note) = items.none { it.id == item.id }

	override fun getSortComparator(@SortField sortField: String): Comparator<Note> {
		return when (sortField) {
			C.FIELD_DATE -> timeComparator
			else -> titleComparator
		}
	}

	fun requestNoteAddition() {
		headerParams.requestFocus = true
		notifyHeaderChanged()
	}

	fun submitCurrentText() = headerTextFieldRef?.get()?.submitText()

	fun setListener(listener: Listener) { this.listener = listener }

	private fun addNoteWithin(text: String) {
		listener?.onNewNote(Note(body = text))
	}

	class HeaderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
		interface Listener {
			fun onTextSubmited(text: String)
		}
		var listener: Listener? = null

		private val textField = itemView as EditText

		private val keyboardListener = TextView.OnEditorActionListener { _, keyCode, _ ->
			val submit = keyCode == EditorInfo.IME_ACTION_DONE

			if (submit) submitText()

			submit
		}

		init {
			textField.setOnEditorActionListener(keyboardListener)
			textField.setOnFocusChangeListener { view, hasFocus ->
				if (!hasFocus) view.context.hideKeyboard(view)
			}
		}

		fun bind(params: HeaderParams) {
			if (params.requestFocus) {
				itemView.postDelayed({ itemView.context.showKeybouard(textField) }, 200)
			}
		}

		fun submitText() {
			val text = textField.text
			if (text.isNotEmpty()) {
				listener?.onTextSubmited(text.toString())
				text.clear()
			}
		}
	}

	class FooterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

	class ViewHolder(binding: NotesListItemBinding): BasicRecyclerAdapter.ViewHolder<Note, NotesListItemBinding>(binding) {
		interface Listener {
			fun onItemClicked()
			fun onImportantButtonClicked()
			fun onCompletedButtonClicked()
			fun onDeleteButtonClicked()
		}
		var listener: Listener? = null

		val titleField = ObservableField<String>("")
		val dateField = ObservableField<String>("")
		val backColor = ObservableInt()
		val isImportant = ObservableBoolean()

		init {
			binding.setVariable(BR.viewModel, this)
		}

		override fun bind(data: Note) {
			backColor.set(data.color)
			titleField.set(data.getListTitle(itemView.context))
			dateField.set(data.getDateCreated(DateFormat.SHORT))
			isImportant.set(data.isImportant)
		}

		fun onClick() = listener?.onItemClicked()

		fun onImportantButtonClick() = listener?.onImportantButtonClicked()

		fun onCompletedButtonClick() = listener?.onCompletedButtonClicked()

		fun onDeleteButtonClick() = listener?.onDeleteButtonClicked()
	}

	class DiffCallback(private val oldItems: List<Note>,
										 private val newItems: List<Note>)
		: HeadedRecyclerDiffUtil.Callback() {

		override fun getOldListSize() = oldItems.size

		override fun getNewListSize() = newItems.size

		override fun areItemsTheSame(oldPos: Int, newPos: Int) = oldItems[oldPos].id == newItems[newPos].id

		override fun areContentsTheSame(oldPos: Int, newPos: Int) = oldItems[oldPos] == newItems[newPos]
	}
}