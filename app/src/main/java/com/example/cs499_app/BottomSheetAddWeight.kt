package com.example.cs499_app

import com.example.cs499_app.databinding.BottomSheetAddRecordBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetAddWeight : BottomSheetDialogFragment(){
    private var _binding: BottomSheetAddRecordBinding? = null
    private val binding get() = _binding!!

    interface AddWeightListener {
        fun onAddWeightSet(weight: Double)
    }
}