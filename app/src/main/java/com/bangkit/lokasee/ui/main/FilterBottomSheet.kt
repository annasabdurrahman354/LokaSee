package com.bangkit.lokasee.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bangkit.lokasee.data.store.AREA_MAX
import com.bangkit.lokasee.data.store.AREA_MIN
import com.bangkit.lokasee.data.store.FilterStore.currentFilter
import com.bangkit.lokasee.data.store.FilterStore.liveFilter
import com.bangkit.lokasee.data.store.PRICE_MAX
import com.bangkit.lokasee.data.store.PRICE_MIN
import com.bangkit.lokasee.databinding.ModalFilterBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterBottomSheet : BottomSheetDialogFragment() {
    private var tempFilter: HashMap<String, Int?> = HashMap()
    private lateinit var binding: ModalFilterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ModalFilterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tempFilter = currentFilter
        with(binding){
            if (tempFilter[AREA_MIN] != null)
                inputFilterAreaMin.editText?.setText(tempFilter[AREA_MIN].toString())
            if (tempFilter[AREA_MAX] != null)
                inputFilterAreaMax.editText?.setText(tempFilter[AREA_MAX].toString())
            if (tempFilter[PRICE_MIN] != null)
                inputFilterPriceMin.editText?.setText(tempFilter[PRICE_MIN].toString())
            if (tempFilter[PRICE_MAX] != null)
                inputFilterPriceMax.editText?.setText(tempFilter[PRICE_MAX].toString())

            btnClearFilter.setOnClickListener{
                for ((key, value) in tempFilter) {
                    tempFilter[key] = null
                }
                inputFilterAreaMax.editText?.text?.clear()
                inputFilterAreaMin.editText?.text?.clear()
                inputFilterPriceMax.editText?.text?.clear()
                inputFilterPriceMin.editText?.text?.clear()
            }

            btnApplyFilter.setOnClickListener{
                var areaMin: Int? = inputFilterAreaMin.editText?.text.toString().toIntOrNull()
                var areaMax: Int? = inputFilterAreaMax.editText?.text.toString().toIntOrNull()
                var priceMin: Int? = inputFilterPriceMin.editText?.text.toString().toIntOrNull()
                var priceMax: Int? = inputFilterPriceMax.editText?.text.toString().toIntOrNull()
                if (areaMin != null && areaMax != null){
                    if(areaMin > areaMax) {
                        inputFilterAreaMax.isErrorEnabled = true
                        inputFilterAreaMax.error = "Area maximum < area minimum"
                    }
                }
                else {
                    inputFilterAreaMax.isErrorEnabled = false
                    inputFilterAreaMax.error = null
                }

                if (priceMin != null && priceMax != null){
                    if(priceMin > priceMax) {
                        inputFilterPriceMax.isErrorEnabled = true
                        inputFilterPriceMax.error = "Price maximum < price minimum"
                    }
                }
                else {
                    inputFilterPriceMax.isErrorEnabled = false
                    inputFilterPriceMax.error = null
                }

                if (inputFilterPriceMax.error == null && inputFilterAreaMax.error == null) {
                    tempFilter[AREA_MIN] = inputFilterAreaMin.editText?.text.toString().toIntOrNull()
                    tempFilter[AREA_MAX] = inputFilterAreaMax.editText?.text.toString().toIntOrNull()
                    tempFilter[PRICE_MIN] = inputFilterPriceMin.editText?.text.toString().toIntOrNull()
                    tempFilter[PRICE_MAX] = inputFilterPriceMax.editText?.text.toString().toIntOrNull()
                    currentFilter = tempFilter
                    liveFilter.value = currentFilter
                    for ((key, value) in currentFilter) {
                        Log.e(key, value.toString())
                    }
                    dismiss()
                }
            }
        }
    }

    companion object {
        const val TAG = "CustomBottomSheetDialogFragment"
        fun newInstance(): FilterBottomSheet {
            return FilterBottomSheet()
        }
    }
}