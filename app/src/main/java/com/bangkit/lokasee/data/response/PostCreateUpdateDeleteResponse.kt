package com.bangkit.lokasee.data.response

import android.os.Parcelable
import com.bangkit.lokasee.data.Post
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PostCreateUpdateDeleteResponse(

	@field:SerializedName("data")
	val data: Post? = null,

	@field:SerializedName("message")
	val message: String
) : Parcelable