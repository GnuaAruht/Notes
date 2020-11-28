package com.thuraaung.notes.frag.edit

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.thuraaung.notes.R
import com.thuraaung.notes.model.NoteModel


class EditNoteFragment : Fragment (R.layout.fragment_edit_note) {

    private val args : EditNoteFragmentArgs by navArgs()
    private lateinit var note : NoteModel

    private lateinit var etTitle : EditText
    private lateinit var etNote : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        note = args.note
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etTitle = view.findViewById(R.id.et_title)
        etNote = view.findViewById(R.id.et_note)

        etTitle.setText(note.title)
        etNote.setText(note.note)

    }
}