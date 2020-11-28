package com.thuraaung.notes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.thuraaung.notes.model.AppUser
import com.thuraaung.notes.uitls.Constants.ALL_USER
import com.thuraaung.notes.uitls.Constants.RC_SIGN_IN
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {

    @Inject
    lateinit var mGoogleSignInClient : GoogleSignInClient

    @Inject
    lateinit var mAuth : FirebaseAuth

    @Inject
    lateinit var firebaseFireStore : FirebaseFirestore

    private lateinit var btnSignIn : SignInButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        btnSignIn = findViewById(R.id.btn_sign_in)
        btnSignIn.setOnClickListener {
            signIn()
        }
    }

    override fun onStart() {
        super.onStart()
        if (mAuth.currentUser != null)
            startMainActivity()
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    @Suppress("DEPRECATION")
    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {

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
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    saveUser(
                        doOnSuccess = { startMainActivity() },
                        doOnFailure = { showErrorMessage() } )
                } else {
                    showErrorMessage()
                }
            }
    }

    private fun saveUser(doOnSuccess : () -> Unit,doOnFailure : () -> Unit) {

        firebaseFireStore.collection(ALL_USER)
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

        Toast.makeText(this,"Authentication failed", Toast.LENGTH_SHORT).show()

    }
}