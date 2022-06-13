package com.bangkit.lokasee.data.response

import android.os.Parcelable
import com.bangkit.lokasee.data.Kabupaten
import com.bangkit.lokasee.data.Kecamatan
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KecamatanListResponse(

	@field:SerializedName("data")
	val data: List<Kecamatan>? = null,

	@field:SerializedName("message")
	val message: String
) : Parcelable