package com.thuraaung.notes.frag.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.transition.TransitionInflater
import com.thuraaung.notes.R
import com.thuraaung.notes.frag.list.NoteListAdapter
import com.thuraaung.notes.frag.list.NoteListFragmentDirections
import com.thuraaung.notes.frag.list.NoteListViewModel


class SearchNoteFragment : DialogFragment() {

    private val viewModel : NoteListViewModel by activityViewModels()

    private val noteListAdapter = NoteListAdapter { note ->
        val action = SearchNoteFragmentDirections.actionSearchNoteFragmentToNoteAddFragment(note)
        findNavController().navigate(action)
    }

    private lateinit var rvNoteSearch : RecyclerView
    private lateinit var etSearch : EditText
    private lateinit var btnCancel : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL,R.style.Theme_Notes_DialogFullScreen)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnCancel = view.findViewById(R.id.btn_cancel)
        etSearch = view.findViewById(R.id.et_search)
        etSearch.requestFocus()

        noteListAdapter.updateNoteList(viewModel.filterNote(""))

        etSearch.doAfterTextChanged { text ->
            noteListAdapter.updateNoteList(viewModel.filterNote(text.toString()))
        }

        btnCancel.setOnClickListener {
            dismiss()
        }

        rvNoteSearch = view.findViewById(R.id.rv_search_note)
        rvNoteSearch.apply {
            layoutManager = StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL)
            adapter = noteListAdapter
        }

    }
}