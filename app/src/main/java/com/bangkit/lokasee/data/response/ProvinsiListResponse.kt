package com.bangkit.lokasee.data.response

import android.os.Parcelable
import com.bangkit.lokasee.data.Kabupaten
import com.bangkit.lokasee.data.Provinsi
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProvinsiListResponse(

	@field:SerializedName("data")
	val data: List<Provinsi>? = null,

	@field:SerializedName("message")
	val message: String
) : Parcelable