package com.thuraaung.notes.di

import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
class SignInModule {

    @Singleton
    @Provides
    fun provideGoogleSingInOption() : GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("575070714644-q6tim77lhf6qe1tuj370f1ge3l38bqj8.apps.googleusercontent.com")
                .requestEmail()
                .build()
    }

    @Singleton
    @Provides
    fun provideGoogleSignInClient(
            @ApplicationContext context: Context,
            gso : GoogleSignInOptions) : GoogleSignInClient {

        return GoogleSignIn.getClient(context, gso)
    }


    @Provides
    fun provideFirebaseAuth() : FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    fun provideFirebaseFireStore() : FirebaseFirestore {
        return Firebase.firestore
    }


    @Provides
    fun provideSharedPreference(@ApplicationContext context : Context) : SharedPreferences {
        return context.getSharedPreferences("APP_PREF",Context.MODE_PRIVATE)
    }

}