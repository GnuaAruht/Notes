package com.thuraaung.notes.uitls

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object DateFormatter {

    @SuppressLint("SimpleDateFormat")
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a")

    fun formatDate(date : Date) : String {
        return dateFormat.format(date)
    }
}