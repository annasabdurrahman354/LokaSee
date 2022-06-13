package com.bangkit.lokasee.data.response

import android.os.Parcelable
import com.bangkit.lokasee.data.Kabupaten
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KabupatenListResponse(

	@field:SerializedName("data")
	val data: List<Kabupaten>? = null,

	@field:SerializedName("message")
	val message: String
) : Parcelable