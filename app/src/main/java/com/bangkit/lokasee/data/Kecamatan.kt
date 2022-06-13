package com.bangkit.lokasee.data

import android.os.Parcelable
import com.bangkit.lokasee.util.capitalizeWords
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Kecamatan(

	@field:SerializedName("kabupaten_id")
	val kabupatenId: Int,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("title")
	val title: String
) : Parcelable {

	override fun toString(): String {
		return title
	}
}