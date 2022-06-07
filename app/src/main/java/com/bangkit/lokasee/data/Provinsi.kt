package com.bangkit.lokasee.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Provinsi(

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("title")
	val title: String
) : Parcelable