package com.thuraaung.notes.frag.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.thuraaung.notes.R
import com.thuraaung.notes.databinding.FragmentNoteListBinding
import com.thuraaung.notes.model.NoteModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteListFragment : Fragment() {

    private val noteListViewModel : NoteListViewModel by activityViewModels()
    private lateinit var binding : FragmentNoteListBinding

    private val noteListAdapter = NoteListAdapter { note  ->
        navigateToNoteAdd(note)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentNoteListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvNote.apply {
            layoutManager = StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL)
            adapter = noteListAdapter
        }

        binding.fabNew.setOnClickListener {
            navigateToNoteAdd(null)
        }

        binding.etSearch.setOnClickListener {
            val extras = FragmentNavigatorExtras(binding.etSearch to "note_search")
            findNavController().navigate(R.id.action_noteList_to_searchNote,null,null,extras)
        }

        noteListViewModel.allNotes.observe(viewLifecycleOwner, { list ->
            noteListAdapter.updateNoteList(list)
        })
    }

    private fun navigateToNoteAdd(note : NoteModel?) {
        val action = NoteListFragmentDirections.actionNoteListToNoteAdd(note)
        findNavController().navigate(action)
    }
}