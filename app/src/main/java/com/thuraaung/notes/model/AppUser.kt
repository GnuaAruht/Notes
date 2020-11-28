package com.thuraaung.notes.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class AppUser(
        val uid : String = "",
        val email : String = "",
        val signInDate : Date = Date()) : Parcelable