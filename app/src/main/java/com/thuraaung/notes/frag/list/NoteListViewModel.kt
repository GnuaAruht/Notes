package com.thuraaung.notes.frag.list

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.thuraaung.notes.model.NoteModel
import com.thuraaung.notes.uitls.Constants.ALL_NOTE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteListViewModel @ViewModelInject constructor(
    private val mAuth : FirebaseAuth,
    private val db : FirebaseFirestore
) : ViewModel() {

    private val _allNotes = MutableLiveData<List<NoteModel>>()
    val allNotes : LiveData<List<NoteModel>>
        get() = _allNotes

    init {
        loadNoteAllNote()
    }

    private fun loadNoteAllNote() {

        viewModelScope.launch(Dispatchers.IO) {

            db.collection(ALL_NOTE)
                .whereEqualTo("ownerUid",mAuth.currentUser!!.uid)
                .addSnapshotListener(MetadataChanges.INCLUDE) { value, _ ->

                    value?.let {

                        val tempNoteList = mutableListOf<NoteModel>()
                        for(doc in it.iterator()) {
                            val note = doc.toObject(NoteModel::class.java)
                            tempNoteList.add(note)
                        }
                        _allNotes.postValue(tempNoteList)
                    }

                }
        }
    }

    fun filterNote(query : String) : List<NoteModel> {

        return _allNotes.value?.let { noteList ->
            noteList.filter { note ->
                        note.title.contains(query,true)
                        || note.note.contains(query,true) }
        } ?: listOf()

    }

    fun updateNote(noteId : Int,noteData : Map<String,Any>,
                   doOnSuccess: () -> Unit,
                   doOnFailure: () -> Unit) {

        viewModelScope.launch(Dispatchers.IO) {

            db.collection(ALL_NOTE)
                .document("${noteId}")
                .update(noteData)
                .addOnSuccessListener {
                    doOnSuccess.invoke()
                }
                .addOnFailureListener {
                    doOnFailure.invoke()
                }
        }
    }

    fun saveNote(note : NoteModel,doOnSuccess: () -> Unit,doOnFailure: () -> Unit) {

        viewModelScope.launch(Dispatchers.IO) {

            db.collection(ALL_NOTE)
                .document("${note.id}")
                .set(note)
                .addOnSuccessListener {
                    doOnSuccess.invoke()
                }
                .addOnFailureListener {
                    doOnFailure.invoke()
                }
        }
    }

    fun deleteNote(note : NoteModel,doOnSuccess: () -> Unit,doOnFailure: () -> Unit) {

        viewModelScope.launch(Dispatchers.IO) {

            db.collection(ALL_NOTE)
                .document("${note.id}")
                .delete()
                .addOnSuccessListener {
                    doOnSuccess.invoke()
                }
                .addOnFailureListener {
                    doOnFailure.invoke()
                }

        }
    }

}