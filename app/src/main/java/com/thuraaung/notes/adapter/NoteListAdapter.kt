package com.thuraaung.notes.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.thuraaung.notes.uitls.DateFormatter
import com.thuraaung.notes.R
import com.thuraaung.notes.model.NoteModel

class NoteListAdapter(private val noteClickListener :
                      ((view : View,note : NoteModel) -> Unit)? = null) : RecyclerView.Adapter<NoteListAdapter.NoteViewHolder>() {

    private var noteList = emptyList<NoteModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_note_item,parent,false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(noteList[position])
    }

    override fun getItemCount(): Int = noteList.size

    fun updateNoteList(note : List<NoteModel>) {
        noteList = note
        notifyDataSetChanged()
    }

    inner class NoteViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        private val tvTitle = view.findViewById<TextView>(R.id.tv_title)
        private val tvNote = view.findViewById<TextView>(R.id.tv_note)
        private val tvDate = view.findViewById<TextView>(R.id.tv_date)

        init {
            view.setOnClickListener {
                noteClickListener?.invoke(it,noteList[adapterPosition])
            }
        }

        fun bind(note : NoteModel) {

            tvTitle.text = note.title
            tvTitle.visibility = if(note.title.isEmpty()) View.GONE else View.VISIBLE
            tvNote.text = note.note
            tvDate.text = DateFormatter.formatDate(note.creationDate)
        }
    }
}