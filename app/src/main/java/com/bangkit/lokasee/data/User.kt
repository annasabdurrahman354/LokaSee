package com.bangkit.lokasee.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("email")
	val email: String,

	@field:SerializedName("phone_number")
	val phoneNumber: String,

	@field:SerializedName("address")
	var address: String,

	@field:SerializedName("address")
	var address: String,

	@field:SerializedName("avatar_url")
	val avatarUrl: String? = null,

	@field:SerializedName("provinsi_id")
	var provinsiId: Int? = null,

	@field:SerializedName("provinsi")
	var provinsi: Provinsi? = null,

	@field:SerializedName("kabupaten_id")
	var kabupatenId: Int? = null,

	@field:SerializedName("kabupaten")
	var kabupaten: Kabupaten? = null,

	@field:SerializedName("kecamatan_id")
	var kecamatanId: Int? = null,

	@field:SerializedName("kecamatan")
	var kecamatan: Kecamatan? = null,

	@field:SerializedName("provinsi_id")
	var provinsiId: Int? = null,

	@field:SerializedName("provinsi")
	var provinsi: Provinsi? = null,

	@field:SerializedName("kabupaten_id")
	var kabupatenId: Int? = null,

	@field:SerializedName("kabupaten")
	var kabupaten: Kabupaten? = null,

	@field:SerializedName("kecamatan_id")
	var kecamatanId: Int? = null,

	@field:SerializedName("kecamatan")
	var kecamatan: Kecamatan? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("email_verified_at")
	val emailVerifiedAt: String? = null,
) : Parcelable