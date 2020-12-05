package com.thuraaung.notes.uitls

import com.google.android.gms.auth.api.signin.GoogleSignInClient


fun GoogleSignInClient.revokeAccess(
    doOnSuccess : (() -> Unit)? = null,
    doOnFailed : (() -> Unit)? = null) {

    revokeAccess()
        .addOnCompleteListener {
            doOnSuccess?.invoke()
        }.addOnFailureListener {
            doOnFailed?.invoke()
        }
}

fun GoogleSignInClient.signOutUser(
    doOnSuccess: (() -> Unit)?= null,
    doOnFailed: (() -> Unit)? = null) {

    signOut()
        .addOnSuccessListener {
            doOnSuccess?.invoke()
        }
        .addOnFailureListener {
            doOnFailed?.invoke()
        }
}