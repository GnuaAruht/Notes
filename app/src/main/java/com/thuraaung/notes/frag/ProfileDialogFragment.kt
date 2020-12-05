package com.thuraaung.notes.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.thuraaung.notes.R
import com.thuraaung.notes.databinding.FragmentProfileDialogBinding
import com.thuraaung.notes.uitls.revokeAccess
import com.thuraaung.notes.uitls.signOutUser
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileDialogFragment(private val signOut : (() -> Unit)? = null) : DialogFragment() {

    @Inject
    lateinit var mAuth : FirebaseAuth
    @Inject
    lateinit var mGoogleSignInClient : GoogleSignInClient

    private lateinit var binding : FragmentProfileDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileDialogBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lytSignOut.setOnClickListener {
            dismiss()
            signOut?.invoke()
        }

        mAuth.currentUser?.let {

            for (profile in it.providerData) {

                val name = profile.displayName
                val email = profile.email
                val photoUrl = profile.photoUrl

                binding.tvUserName.text = name
                binding.tvEmail.text = email
                binding.imgProfile.load(photoUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ic_account_circle_24)
                    transformations(CircleCropTransformation())
                }

            }
        }
    }
}