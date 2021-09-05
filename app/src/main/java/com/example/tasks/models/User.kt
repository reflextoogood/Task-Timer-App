package com.example.tasks.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(
    val id:String="",
    val name:String="",
    val email:String=""):Parcelable

