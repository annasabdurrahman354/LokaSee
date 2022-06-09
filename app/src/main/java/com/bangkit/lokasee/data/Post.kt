package com.bangkit.lokasee.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Post(
	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("desc")
	val desc: String,

	@field:SerializedName("images")
	val images: List<String>,

	@field:SerializedName("area")
	val area: Int,

	@field:SerializedName("price")
	val price: Int,

	@field:SerializedName("address")
	val address: String,

	@field:SerializedName("latitude")
	val latitude: Double,

	@field:SerializedName("longitude")
	val longitude: Double,

	@field:SerializedName("user_id")
	val userId: Int,

	@field:SerializedName("user")
	val user: User? = null,

	@field:SerializedName("provinsi_id")
	val provinsiId: Int,

	@field:SerializedName("provinsi")
	val provinsi: Provinsi? = null,

	@field:SerializedName("kabupaten_id")
	val kabupatenId: Int,

	@field:SerializedName("kabupaten")
	val kabupaten: Kabupaten? = null,

	@field:SerializedName("kecamatan_id")
	val kecamatanId: Int,

	@field:SerializedName("kecamatan")
	val kecamatan: Kecamatan? = null,

	@field:SerializedName("created_at")
	val createdAt: String,

	@field:SerializedName("updated_at")
	val updatedAt: String,

	@field:SerializedName("deleted_at")
	val deletedAt: String? = null,
) : Parcelable