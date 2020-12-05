package com.thuraaung.notes.frag

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.thuraaung.notes.R
import com.thuraaung.notes.databinding.FragmentSignInBinding
import com.thuraaung.notes.model.AppUser
import com.thuraaung.notes.uitls.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class SignInFragment : Fragment() {

    @Inject
    lateinit var mGoogleSignInClient : GoogleSignInClient

    @Inject
    lateinit var mAuth : FirebaseAuth

    @Inject
    lateinit var firebaseFireStore : FirebaseFirestore

    private lateinit var binding : FragmentSignInBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(inflater)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        mAuth.currentUser?.let {
            navigateToNoteList()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignIn.setOnClickListener {
            signIn()
        }
    }

    @Suppress("DEPRECATION")
    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, Constants.RC_SIGN_IN)
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.RC_SIGN_IN) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.e("LoginActivity","Google sign failed : ${e.printStackTrace()}")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {

        lifecycleScope.launch(Dispatchers.Main) {

            val credential = GoogleAuthProvider.getCredential(idToken, null)
            mAuth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        saveUser(
                            doOnSuccess = { navigateToNoteList() },
                            doOnFailure = { showErrorMessage() } )
                    } else {
                        showErrorMessage()
                    }
                }

        }
    }


    private fun saveUser(doOnSuccess : () -> Unit,doOnFailure : () -> Unit) {

        firebaseFireStore.collection(Constants.ALL_USER)
            .document(mAuth.currentUser!!.email.toString())
            .set(AppUser(mAuth.currentUser!!.uid,mAuth.currentUser!!.email!!, Date()))
            .addOnSuccessListener {
                doOnSuccess.invoke()
            }
            .addOnFailureListener {
                doOnFailure.invoke()
            }
    }

    private fun showErrorMessage() {

    }

    private fun navigateToNoteList() {
        findNavController().navigate(R.id.action_sign_in_to_noteList)
    }
}