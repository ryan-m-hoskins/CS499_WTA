package com.example.cs499_app

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cs499_app.databinding.BottomSheetTargetWeightBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetTargetWeight : BottomSheetDialogFragment() {
    private var _binding: BottomSheetTargetWeightBinding? = null
    private val binding get() = _binding!!

    interface TargetWeightListener {
        fun onTargetWeightSet(weight: Double)
    }

    private var targetWeightListener: TargetWeightListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is TargetWeightListener) {
            targetWeightListener = context
        }
        else {
            throw RuntimeException("$context must implement TargetWeightListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomSheetTargetWeightBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.submitTargetWeightButton.setOnClickListener {
            val weightText = binding.enterWeight.text.toString()
            if (weightText.isEmpty()) {
                binding.enterWeight.error = "Please enter a weight"
                return@setOnClickListener
            }

            val weight = weightText.toDoubleOrNull()
            if (weight == null) {
                binding.enterWeight.error = "Please enter a valid number"
                return@setOnClickListener
            }

            if (weight <= 0) {
                binding.enterWeight.error = "Weight must be greater than 0"
                return@setOnClickListener
            }

            targetWeightListener?.onTargetWeightSet(weight)
            dismiss()
        }

        binding.targetWeightCancelButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "BottomSheetTargetWeight"
    }
}