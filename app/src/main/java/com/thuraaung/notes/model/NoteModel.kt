package com.thuraaung.notes.model

import android.os.Parcelable
import com.thuraaung.notes.uitls.getRandomNumber
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class NoteModel(
    val id : Int = getRandomNumber(),
    val title : String = "",
    val note : String = "",
    val color : String = "#ffffff",
    val creationDate : Date = Date(),
    val modifiedDate : Date = Date(),
) : Parcelable