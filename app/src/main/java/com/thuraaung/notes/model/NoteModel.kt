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
    val ownerUid : String = "",
    val ownerEmail : String = "",
    val color : String = "#ffffff",
    val isReadOnly : Boolean = true,
    val creationDate : Date = Date(),
    val modifiedDate : Date = Date(),
    val receivers : List<AppUser> = listOf()
) : Parcelable