package com.bangkit.lokasee.data

import android.os.Parcelable
import com.bangkit.lokasee.data.retrofit.ApiConfig
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Post(
	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("title")
	var title: String,

	@field:SerializedName("desc")
	var desc: String,

	@field:SerializedName("images")
	var images: List<String>,

	@field:SerializedName("area")
	var area: Int,

	@field:SerializedName("price")
	var price: Long,

	@field:SerializedName("address")
	var address: String,

	@field:SerializedName("latitude")
	var latitude: Double,

	@field:SerializedName("longitude")
	var longitude: Double,

	@field:SerializedName("rating_restaurant")
	var ratingRestaurang: String?,

	@field:SerializedName("user_id")
	var userId: Int,

	@field:SerializedName("user")
	var user: User? = null,

	@field:SerializedName("provinsi_id")
	var provinsiId: Int,

	@field:SerializedName("provinsi")
	var provinsi: Provinsi? = null,

	@field:SerializedName("kabupaten_id")
	var kabupatenId: Int,

	@field:SerializedName("kabupaten")
	var kabupaten: Kabupaten? = null,

	@field:SerializedName("kecamatan_id")
	var kecamatanId: Int,

	@field:SerializedName("kecamatan")
	var kecamatan: Kecamatan? = null,

	@field:SerializedName("created_at")
	var createdAt: String,

	@field:SerializedName("updated_at")
	var updatedAt: String,

	@field:SerializedName("deleted_at")
	var deletedAt: String? = null,
) : Parcelable