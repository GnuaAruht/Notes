package com.thuraaung.notes.frag.add

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.thuraaung.notes.R
import com.thuraaung.notes.databinding.FragmentNoteAddBinding
import com.thuraaung.notes.databinding.FragmentNoteListBinding
import com.thuraaung.notes.frag.list.NoteListViewModel
import com.thuraaung.notes.model.AppUser
import com.thuraaung.notes.model.NoteModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_noteadd_tool_bar.view.*
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class NoteAddFragment : Fragment() {

    private val args : NoteAddFragmentArgs by navArgs()

    private val viewModel : NoteListViewModel by activityViewModels()
    private lateinit var binding : FragmentNoteAddBinding

    @Inject
    lateinit var mAuth : FirebaseAuth

    private var note : NoteModel? = null
    private var isAddAction : Boolean = true

    private fun isValidData() : Boolean {

        val tempTitle = binding.etTitle.text.toString().trim()
        val tempNote = binding.etNote.text.toString().trim()

        return (tempNote.isNotBlank() && tempNote.isNotEmpty() && if(isAddAction) true else { tempTitle != note!!.title || tempNote != note!!.note })
    }

    private val noteTextWatcher = object : TextWatcher {

        override fun afterTextChanged(s: Editable?) {
            updateActionSave(isValidData())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            updateActionSave(isValidData())
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // no implement
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        note = args.note
        isAddAction = args.note == null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoteAddBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateActionSave(false)

        binding.layoutToolbar.lbl_label.text = if (isAddAction) "Add Note" else "Edit Note"

        binding.etTitle.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.etTitle.addTextChangedListener(noteTextWatcher)
            } else {
                binding.etTitle.removeTextChangedListener(noteTextWatcher)
            }
        }

        binding.etNote.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.etNote.addTextChangedListener(noteTextWatcher)
            } else {
                binding.etNote.removeTextChangedListener(noteTextWatcher)
            }
        }

        binding.layoutToolbar.img_back.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.layoutToolbar.img_save.setOnClickListener {
            saveNote()
            findNavController().popBackStack()
        }


        binding.layoutToolbar.img_delete.visibility = if (isAddAction) View.GONE else View.VISIBLE
        binding.layoutToolbar.img_delete.setOnClickListener {

            MaterialAlertDialogBuilder(requireContext(),R.style.ThemeOverlay_App_AlertDialog)
                .setTitle("Confirm")
                .setMessage("Are you sure to delete this note?")
                .setPositiveButton("Delete") { _, _ ->
                    viewModel.deleteNote(note = note!!,
                        doOnSuccess = {},
                        doOnFailure = {})
                    findNavController().popBackStack()
                }
                .setNegativeButton("Cancel", null).show()
        }

        binding.etTitle.setText(note?.title ?: "")
        binding.etNote.setText(note?.note ?: "")

    }

    private fun showColorPicker() {

        MaterialColorPickerDialog
            .Builder(requireActivity())
            .setColorShape(ColorShape.CIRCLE)
            .setDefaultColor("#ffffff")
            .setColors(arrayListOf(
                "#ffffff",
                "#f6e58d", "#ffbe76", "#ff7979", "#badc58", "#dff9fb",
                "#7ed6df", "#e056fd", "#686de0", "#30336b", "#95afc0"
            ))
            .setColorListener { color , colorHex ->

                val window = requireActivity().window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//                window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.purple_100)
                window.statusBarColor = color

            }
            .show()
    }

    private fun updateActionSave(isEnable : Boolean) {

        binding.layoutToolbar.img_save.apply {
            isEnabled = isEnable
            imageAlpha = if (isEnable) 255 else 100
        }
    }

    private fun saveNote() {

        val tempTitle = binding.etTitle.text.toString()
        val tempNote = binding.etNote.text.toString()
        val currentMillis = System.currentTimeMillis()

        if (isAddAction) {

            viewModel.saveNote(note = NoteModel(
                title = tempTitle,
                note = tempNote,
                color = "#ffffff",
                ownerUid = mAuth.currentUser!!.uid,
                ownerEmail = mAuth.currentUser!!.email!!,
                receivers = listOf(),
                creationDate = Date(),
                modifiedDate = Date(currentMillis)),
                doOnSuccess = {},
                doOnFailure = {
                    Toast.makeText(context,"Save note failed",Toast.LENGTH_SHORT).show()
                })

        } else {

            viewModel.updateNote(
                noteId = note!!.id,
                noteData = mapOf(
                    "title" to tempTitle,
                    "note" to tempNote,
                    "modifiedDate" to Date(currentMillis),
                    "receivers" to listOf<AppUser>()
                ),
                doOnSuccess = {},
                doOnFailure = {
                    Toast.makeText(context,"Update note failed",Toast.LENGTH_SHORT).show()
                })

        }
    }
}