package com.example.cs499_app

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.cs499_app.databinding.BottomSheetAddRecordBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BottomSheetAddWeight : BottomSheetDialogFragment(){
    private var _binding: BottomSheetAddRecordBinding? = null
    private val binding get() = _binding!!
    private var selectedDate: Calendar = Calendar.getInstance()

    interface AddWeightListener {
        fun onWeightRecordSet(weight: Double, date: Long)
    }

    private var listener: AddWeightListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AddWeightListener) {
            listener = context
        }
        else {
            throw RuntimeException("$context must implement AddWeightListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomSheetAddRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDatePicker()
        setupClickListeners()
    }

    // == Method to Set Up Click Listeners == //
    private fun setupClickListeners() {
        binding.submitRecordButton.setOnClickListener {
            val weightText = binding.enterWeight.text.toString()
            validateAndSubmitWeight(weightText)
        }

        binding.addRecordCancelButton.setOnClickListener {
            dismiss()
        }
    }

    // == Method to Validate Input and Submit Weight Record == //
    private fun validateAndSubmitWeight(weightText: String) {
        when {
            weightText.isEmpty() -> {
                binding.enterWeight.error = getString(R.string.error_empty_weight)
                return
            }
            weightText.toDoubleOrNull() == null -> {
                binding.enterWeight.error = getString(R.string.error_invalid_weight)
                return
            }
            weightText.toDouble() <= 0 -> {
                binding.enterWeight.error = getString(R.string.error_negative_weight)
                return
            }
            selectedDate.timeInMillis > System.currentTimeMillis() -> {
                binding.dateInput.error = getString(R.string.error_future_date)
                return
            }
        }
        // Show loading state
        setLoadingState(true)

        // Check for duplicate date before submitting
        (requireActivity() as? MainActivity)?.checkDateExists(
            selectedDate.timeInMillis,
            onDuplicateFound = {
                setLoadingState(false)
                binding.dateInput.error = getString(R.string.error_duplicate_date)
                Toast.makeText(context, getString(R.string.error_duplicate_date), Toast.LENGTH_SHORT).show()
                               },
            onDateAvailable = {
                setLoadingState(false)
                listener?.onWeightRecordSet(weightText.toDouble(), selectedDate.timeInMillis)
                dismiss()
            },
            onError = { error ->
                setLoadingState(false)
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun setLoadingState(loading: Boolean) {
        binding.submitRecordButton.isEnabled = !loading
        binding.addRecordCancelButton.isEnabled = !loading
        binding.enterWeight.isEnabled = !loading
        binding.dateInput.isEnabled = !loading
        // Optionally add a progress indicator
    }

    private fun setupDatePicker() {
        updateDateDisplay()

        binding.dateInput.setOnClickListener{
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                selectedDate.set(year, month, day)
                if (selectedDate.timeInMillis <= System.currentTimeMillis()) {
                    updateDateDisplay()
                    binding.dateInput.error = null
                }
                else {
                    selectedDate = Calendar.getInstance()
                    updateDateDisplay()
                    binding.dateInput.error = getString(R.string.error_future_date)
                }
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        ).apply{
            datePicker.maxDate = System.currentTimeMillis()
            show()
        }
    }

    private fun updateDateDisplay() {
        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        binding.dateInput.setText(dateFormat.format(selectedDate.time))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "BottomSheetAddWeight"
    }
}