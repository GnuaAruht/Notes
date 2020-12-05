package com.thuraaung.notes.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.thuraaung.notes.R
import com.thuraaung.notes.adapter.NoteListAdapter
import com.thuraaung.notes.databinding.FragmentNoteListBinding
import com.thuraaung.notes.model.NoteModel
import com.thuraaung.notes.uitls.NetworkUtils
import com.thuraaung.notes.uitls.revokeAccess
import com.thuraaung.notes.uitls.signOutUser
import com.thuraaung.notes.vm.NoteListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NoteListFragment : Fragment() {

    @Inject
    lateinit var mAuth: FirebaseAuth

    @Inject
    lateinit var mGoogleSignInClient: GoogleSignInClient

    private val noteListViewModel: NoteListViewModel by activityViewModels()
    private lateinit var binding: FragmentNoteListBinding

    private val noteListAdapter = NoteListAdapter { _, note ->
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

        binding.toolbar.imgProfile.setOnClickListener {
            val profileDialog = ProfileDialogFragment {
                MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_AlertDialog)
                    .setTitle("Confirm")
                    .setMessage("Are you sure to sign out?")
                    .setPositiveButton("Sign out") { _, _ ->
                        signOutUser()
                    }
                    .setNegativeButton("Cancel", null).show()
            }
            profileDialog.show(childFragmentManager, "")
        }

        binding.rvNote.apply {
            layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
            adapter = noteListAdapter
        }

        binding.fabNew.setOnClickListener {
            navigateToNoteAdd(null)
        }

        binding.etSearch.setOnClickListener {
            val extras = FragmentNavigatorExtras(binding.etSearch to "note_search")
            findNavController().navigate(R.id.action_noteList_to_searchNote, null, null, extras)
        }

        noteListViewModel.allNotes.observe(viewLifecycleOwner, { list ->
            if (list.isEmpty()) {
                binding.lblNoNote.visibility = View.VISIBLE
                binding.layoutNoteList.visibility = View.GONE
            } else {
                binding.lblNoNote.visibility = View.GONE
                binding.layoutNoteList.visibility = View.VISIBLE
            }
            noteListAdapter.updateNoteList(list)
        })

        mAuth.currentUser?.let {

            for (profile in it.providerData) {
                binding.toolbar.imgProfile.load(profile.photoUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ic_account_circle_24)
                    transformations(CircleCropTransformation())
                }
            }
        }

        NetworkUtils.getNetworkLiveData(requireContext()).observe(viewLifecycleOwner) {
            binding.lblNoInternet.visibility = if (it) View.GONE else View.VISIBLE
        }
    }

    private fun navigateToNoteAdd(note: NoteModel?) {
        val action = NoteListFragmentDirections.actionNoteListToNoteAdd(note)
        findNavController().navigate(action)
    }


    private fun signOutUser() {

        lifecycleScope.launch(Dispatchers.Main) {

            mGoogleSignInClient.revokeAccess(
                doOnSuccess = {
                    mGoogleSignInClient.signOutUser(
                        doOnSuccess = {
                            mAuth.signOut()
                            findNavController().navigate(R.id.action_sign_out)
                        },
                        doOnFailed = { showErrorSignOutFailed() }
                    )
                },
                doOnFailed = { showErrorSignOutFailed() })

        }
    }

    private fun showErrorSignOutFailed() {
        Toast.makeText(context, "Sign out failed", Toast.LENGTH_SHORT).show()
    }
}