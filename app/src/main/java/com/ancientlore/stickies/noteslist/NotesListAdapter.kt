package com.ancientlore.stickies.noteslist

import android.content.Context
import android.support.annotation.UiThread
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import com.ancientlore.stickies.BasicRecyclerAdapter
import com.ancientlore.stickies.C
import com.ancientlore.stickies.R
import com.ancientlore.stickies.SortField
import com.ancientlore.stickies.data.model.Note
import com.ancientlore.stickies.utils.recyclerdiff.HeadedRecyclerDiffUtil
import com.ancientlore.stickies.utils.getListTitle
import com.ancientlore.stickies.utils.hideKeyboard
import com.ancientlore.stickies.utils.showKeybouard
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.lang.ref.WeakReference
import java.text.DateFormat
import java.util.*

class NotesListAdapter(context: Context, items: MutableList<Note>)
	: BasicRecyclerAdapter<Note, NotesListAdapter.ViewHolder>(context, items, true, true) {

	data class HeaderParams(var requestFocus: Boolean = false)

	private var timeComparator = Comparator<Note> { o1, o2 -> o1.timeCreated.compareTo(o2.timeCreated) }
	private var titleComparator = Comparator<Note> { o1, o2 -> o1.compareByText(o2) }

	private var headerParams = HeaderParams()

	private var headerTextFieldRef: WeakReference<HeaderViewHolder>? = null

	private val onNewNoteEvent = PublishSubject.create<Note>()

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

	override fun getViewHolderLayoutRes(viewType: Int) = R.layout.notes_list_item

	override fun getViewHolder(layout: View) = ViewHolder(layout)

	override fun getDiffCallback(newItems: List<Note>) = DiffCallback(items, newItems)

	@UiThread
	override fun deleteItem(id: Long) = deleteItemAt(findPosition(id))

	override fun findItem(id: Long) = items.find { it.id == id }

	override fun findPosition(id: Long) = items.indexOfFirst { it.id == id }

	override fun isTheSame(first: Note, second: Note) = first.id == second.id

	override fun isUnique(item: Note) = items.none { it.id == item.id }

	override fun getSortComparator(@SortField sortField: String) = when (sortField) {
		C.FIELD_DATE -> timeComparator
		else -> titleComparator
	}

	override fun observeNewItem() = onNewNoteEvent as Observable<Note>

	fun requestNoteAddition() {
		headerParams.requestFocus = true
		notifyHeaderChanged()
	}

	fun submitCurrentText() = headerTextFieldRef?.get()?.submitText()

	private fun addNoteWithin(text: String) {
		val newNote = Note(body = text)
		onNewNoteEvent.onNext(newNote)
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

	class ViewHolder(itemView: View): BasicRecyclerAdapter.ViewHolder<Note>(itemView) {
		private val titleView = itemView.findViewById<TextView>(R.id.titleView)
		private val dateView = itemView.findViewById<TextView>(R.id.dateView)
		private val importanceView = itemView.findViewById<View>(R.id.importanceView)

		override fun bind(data: Note) {
			(itemView as CardView).setCardBackgroundColor(data.color)
			titleView.text = data.getListTitle(itemView.context)
			dateView.text = data.getDateCreated(DateFormat.SHORT)
			importanceView.visibility = if (data.isImportant) View.VISIBLE else View.INVISIBLE
		}
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