package com.bangkit.lokasee.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.bangkit.lokasee.R
import com.bangkit.lokasee.data.Kabupaten
import com.bangkit.lokasee.data.Kecamatan
import com.bangkit.lokasee.data.Provinsi
import com.bangkit.lokasee.data.Result
import com.bangkit.lokasee.data.store.*
import com.bangkit.lokasee.data.store.FilterStore.currentFilter
import com.bangkit.lokasee.data.store.FilterStore.currentKabupaten
import com.bangkit.lokasee.data.store.FilterStore.currentKecamatan
import com.bangkit.lokasee.data.store.FilterStore.currentProvinsi
import com.bangkit.lokasee.data.store.FilterStore.kabupatenList
import com.bangkit.lokasee.data.store.FilterStore.kecamatanList
import com.bangkit.lokasee.data.store.FilterStore.liveFilter
import com.bangkit.lokasee.data.store.FilterStore.locationString
import com.bangkit.lokasee.data.store.FilterStore.provinsiList
import com.bangkit.lokasee.databinding.ModalLocationFilterBinding
import com.bangkit.lokasee.ui.main.map.MapViewModel
import com.bangkit.lokasee.util.ViewHelper.gone
import com.bangkit.lokasee.util.ViewHelper.visible
import com.bangkit.lokasee.util.getKabupaten
import com.bangkit.lokasee.util.getKecamatan
import com.bangkit.lokasee.util.getProvinsi
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class LocationFilterBottomSheet(mainViewModel: MainViewModel) : BottomSheetDialogFragment() {
    private var tempFilter: HashMap<String, Int?> = HashMap()
    private val mainViewModel = mainViewModel
    private lateinit var binding: ModalLocationFilterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ModalLocationFilterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tempFilter = currentFilter
        getProvinsiData()
        with(binding){
            btnClearFilter.setOnClickListener{
                for ((key, value) in tempFilter) {
                    tempFilter[key] = null
                }
                selectFilterProvinsi.editText?.text?.clear()
                selectFilterKabupaten.editText?.text?.clear()
                selectFilterKecamatan.editText?.text?.clear()
                selectFilterKabupaten.gone()
                selectFilterKecamatan.gone()
            }

            btnApplyFilter.setOnClickListener{
                currentFilter[PROVINSI] = tempFilter[PROVINSI]
                currentFilter[KABUPATEN] = tempFilter[KABUPATEN]
                currentFilter[KECAMATAN] = tempFilter[KECAMATAN]
                liveFilter.value = currentFilter
                if(currentFilter[KABUPATEN] != null && currentFilter[KECAMATAN] != null){
                    locationString.value = "${binding.selectFilterKecamatan.editText?.text.toString()}, ${binding.selectFilterKabupaten.editText?.text.toString()}"
                }
                else if(currentFilter[PROVINSI] != null && currentFilter[KABUPATEN] != null && currentFilter[KECAMATAN] == null){
                    locationString.value = "${binding.selectFilterKabupaten.editText?.text.toString()}, ${binding.selectFilterProvinsi.editText?.text.toString()}"
                }
                else if(currentFilter[PROVINSI] != null && currentFilter[KABUPATEN] == null){
                    locationString.value = "${binding.selectFilterProvinsi.editText?.text.toString()}, Indonesia"
                }
                else if (currentFilter[PROVINSI] == null){
                    locationString.value = "Indonesia"
                }
                dismiss()
            }
        }
    }

    private fun getProvinsiData() {
        mainViewModel.getAllProvinsi().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progLocation.visible()
                        binding.selectFilterProvinsi.gone()
                        binding.selectFilterKabupaten.gone()
                        binding.selectFilterKecamatan.gone()
                        binding.lnrApply.gone()
                        binding.btnReload.gone()
                    }
                    is Result.Success -> {
                        val provinsiData = result.data.data
                        if (provinsiData?.isNotEmpty() == true){
                            provinsiList = provinsiData as MutableList<Provinsi>
                        }

                        if (tempFilter[PROVINSI] != null) {
                            val selectedProvinsi = getProvinsi(tempFilter[PROVINSI]!!, provinsiList)
                            binding.selectFilterProvinsi.editText?.setText(selectedProvinsi.title)
                            currentProvinsi = selectedProvinsi
                            getKabupatenByProvinsi(tempFilter[PROVINSI]!!)
                        }

                        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, provinsiList)
                        binding.atvProvinsi.setAdapter(arrayAdapter)
                        binding.selectFilterProvinsi.editText?.addTextChangedListener {
                            if(it.toString() != ""){
                                val selectedProvinsi = getProvinsi(
                                    binding.selectFilterProvinsi.editText?.text.toString(),
                                    provinsiList
                                )
                                binding.selectFilterKabupaten.editText?.text?.clear()
                                binding.selectFilterKecamatan.editText?.text?.clear()
                                tempFilter[PROVINSI] = selectedProvinsi.id
                                tempFilter[KABUPATEN] = null
                                tempFilter[KECAMATAN] = null
                                currentProvinsi = selectedProvinsi
                                getKabupatenByProvinsi(selectedProvinsi.id)
                            }
                        }
                        binding.progLocation.gone()
                        binding.selectFilterProvinsi.visible()
                        binding.lnrApply.visible()
                        binding.btnReload.gone()
                    }
                    is Result.Error -> {
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_LONG).show()
                        binding.progLocation.gone()
                        binding.selectFilterProvinsi.gone()
                        binding.lnrApply.gone()
                        binding.btnReload.visible()
                        binding.btnReload.setOnClickListener {
                            getProvinsiData()
                        }
                    }
                }
            }
        }
    }

    private fun getKabupatenByProvinsi(provinsiId: Int) {
        mainViewModel.getKabupatenByProvinsi(provinsiId).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progLocation.visible()
                        binding.selectFilterKabupaten.gone()
                        binding.selectFilterKecamatan.gone()
                        binding.lnrApply.gone()
                        binding.btnReload.gone()
                    }
                    is Result.Success -> {
                        val kabupatenData = result.data.data
                        if (kabupatenData?.isNotEmpty() == true){
                            kabupatenList = kabupatenData as MutableList<Kabupaten>
                        }

                        if (tempFilter[KABUPATEN] != null) {
                            val selectedKabupaten =  getKabupaten(tempFilter[KABUPATEN]!!,kabupatenList)
                            binding.selectFilterKabupaten.editText?.setText(selectedKabupaten.title)
                            currentKabupaten = selectedKabupaten
                            getKecamatanByKabupaten(tempFilter[KABUPATEN]!!)
                        }

                        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, kabupatenList)
                        binding.atvKabupaten.setAdapter(arrayAdapter)
                        binding.selectFilterKabupaten.editText?.addTextChangedListener {
                            if(it.toString() != ""){
                                val selectedKabupaten = getKabupaten(
                                    binding.selectFilterKabupaten.editText?.text.toString(),
                                    kabupatenList
                                )
                                tempFilter[KABUPATEN] = selectedKabupaten.id
                                tempFilter[KECAMATAN] = null
                                currentKabupaten = selectedKabupaten
                                binding.selectFilterKecamatan.editText?.text?.clear()
                                getKecamatanByKabupaten(selectedKabupaten.id)
                            }
                        }
                        binding.progLocation.gone()
                        binding.selectFilterKabupaten.visible()
                        binding.lnrApply.visible()
                        binding.btnReload.gone()
                    }
                    is Result.Error -> {
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_LONG).show()
                        binding.progLocation.gone()
                        binding.selectFilterKabupaten.gone()
                        binding.lnrApply.gone()
                        binding.btnReload.visible()
                        binding.btnReload.setOnClickListener {
                            getKabupatenByProvinsi(provinsiId)
                        }
                    }
                }
            }
        }
    }

    private fun getKecamatanByKabupaten(kabupatenId: Int) {
        mainViewModel.getKecamatanByKabupaten(kabupatenId).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.selectFilterKecamatan.gone()
                        binding.progLocation.visible()
                        binding.lnrApply.gone()
                        binding.btnReload.gone()
                    }
                    is Result.Success -> {
                        val kecamatanData = result.data.data
                        if (kecamatanData?.isNotEmpty() == true){
                            kecamatanList = kecamatanData as MutableList<Kecamatan>
                        }

                        if (tempFilter[KECAMATAN] != null) {
                            val selectedKecamatan = getKecamatan(tempFilter[KECAMATAN]!!, kecamatanList)
                            binding.selectFilterKecamatan.editText?.setText(selectedKecamatan.title)
                            currentKecamatan = selectedKecamatan
                        }

                        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, kecamatanList)
                        binding.atvKecamatan.setAdapter(arrayAdapter)
                        binding.selectFilterKecamatan.editText?.addTextChangedListener {
                            if(it.toString() != "") {
                                val selectedKecamatan = getKecamatan(
                                    binding.selectFilterKecamatan.editText?.text.toString(),
                                    kecamatanList
                                )
                                tempFilter[KECAMATAN] = selectedKecamatan.id
                                currentKecamatan = selectedKecamatan
                            }
                        }
                        binding.progLocation.gone()
                        binding.selectFilterKecamatan.visible()
                        binding.lnrApply.visible()
                        binding.btnReload.gone()
                    }
                    is Result.Error -> {
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_LONG).show()
                        binding.progLocation.gone()
                        binding.selectFilterKecamatan.gone()
                        binding.lnrApply.gone()
                        binding.btnReload.visible()
                        binding.btnReload.setOnClickListener {
                            getKecamatanByKabupaten(kabupatenId)
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "CustomBottomSheetDialogFragment"
    }
}