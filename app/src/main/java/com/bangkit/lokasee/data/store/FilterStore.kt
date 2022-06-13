package com.bangkit.lokasee.data.store

import androidx.lifecycle.MutableLiveData
import com.bangkit.lokasee.data.Kabupaten
import com.bangkit.lokasee.data.Kecamatan
import com.bangkit.lokasee.data.Provinsi


const val PRICE_MIN = "price_min"
const val PRICE_MAX = "price_max"
const val AREA_MIN = "area_min"
const val AREA_MAX = "area_max"
const val PROVINSI = "provinsi"
const val KABUPATEN = "kabupaten"
const val KECAMATAN = "kecamatan"
//const val TYPE = "type"

object FilterStore {
    val liveFilter = MutableLiveData<HashMap<String, Int?>>()
    val locationString = MutableLiveData<String>()

    var currentFilter : HashMap<String, Int?> = HashMap()
    var provinsiList = mutableListOf<Provinsi>()
    var kabupatenList = mutableListOf<Kabupaten>()
    var kecamatanList = mutableListOf<Kecamatan>()

    var currentProvinsi: Provinsi? = null
    var currentKabupaten: Kabupaten? = null
    var currentKecamatan: Kecamatan? = null

    init {
        currentFilter[PRICE_MIN] = null
        currentFilter[PRICE_MAX] = null
        currentFilter[AREA_MIN] = null
        currentFilter[AREA_MAX] = null
        currentFilter[PROVINSI] = null
        currentFilter[KABUPATEN] = null
        currentFilter[KECAMATAN] = null
        //currentFilter[TYPE] = null
    }
}