package com.thuraaung.notes.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.transition.TransitionInflater
import com.thuraaung.notes.R
import com.thuraaung.notes.databinding.FragmentSearchNoteBinding
import com.thuraaung.notes.adapter.NoteListAdapter
import com.thuraaung.notes.vm.NoteListViewModel


class SearchNoteFragment : DialogFragment() {

    private val viewModel : NoteListViewModel by activityViewModels()

    private val noteListAdapter = NoteListAdapter { _ , note ->
        val action = SearchNoteFragmentDirections.actionSearchNoteToNoteAdd(note)
        findNavController().navigate(action)
    }

    private lateinit var binding : FragmentSearchNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL,R.style.Theme_Notes_DialogFullScreen)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchNoteBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noteListAdapter.updateNoteList(viewModel.filterNote(""))

        binding.etSearch.requestFocus()
        binding.etSearch.doAfterTextChanged { text ->
            val noteList = viewModel.filterNote(text.toString())
            if (noteList.isEmpty()) {
                binding.rvSearchNote.visibility = View.GONE
                binding.lblNoNote.visibility = View.VISIBLE
            } else {
                binding.rvSearchNote.visibility = View.VISIBLE
                binding.lblNoNote.visibility = View.GONE
            }
            noteListAdapter.updateNoteList(noteList)
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.rvSearchNote.apply {
            layoutManager = StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL)
            adapter = noteListAdapter
        }

    }
}