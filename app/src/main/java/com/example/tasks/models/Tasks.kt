package com.example.tasks.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Tasks (
    val Title:String="",
    val id:String="",
    val email:String="",
    val date:String="",
    val time:String="", ):Parcelable