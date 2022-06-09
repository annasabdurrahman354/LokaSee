package com.bangkit.lokasee.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Filter (
    var priceMin: Int? = null,
    var priceMax: Int? = null,
    var areaMin: Int? = null,
    var areaMax: Int? = null,
    var distance: Int? = null,
    var idProvinsi: Int? = null,
    var idKota: Int? = null,
    var idKecamatan: Int? = null
) : Parcelable