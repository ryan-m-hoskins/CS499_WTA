package com.example.cs499_app

import WeightRecord
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.cs499_app.databinding.BottomSheetEditRecordBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BottomSheetEditWeight : BottomSheetDialogFragment() {
    companion object {
        const val TAG = "BottomSheetEditWeight"
    }

    private var _binding : BottomSheetEditRecordBinding? = null
    private val binding get() = _binding!!
    private var selectedDate: Calendar = Calendar.getInstance()
    private lateinit var currentRecord: WeightRecord

    interface EditWeightListener {
        fun onWeightRecordUpdate(weightRecord: WeightRecord)
        fun onWeightRecordDelete(weightRecord: WeightRecord)
    }

    private var listener: EditWeightListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is EditWeightListener) {
            listener = context
        }
        else {
            throw RuntimeException("$context must implement EditWeightListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomSheetEditRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInitialData()
        setupDatePicker()
        setupClickListeners()
    }

    private fun setupInitialData() {
        binding.enterWeight.setText(currentRecord.weight.toString())
        selectedDate.timeInMillis = currentRecord.date
        updateDateDisplay()
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

        binding.deleteButton.setOnClickListener {
            showDeleteConfirmation()
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
        if (selectedDate.timeInMillis != currentRecord.date) {
            (requireActivity() as? MainActivity)?.checkDateExists(
                selectedDate.timeInMillis,
                onDuplicateFound = {
                    setLoadingState(false)
                    binding.dateInput.error = getString(R.string.error_duplicate_date)
                    Toast.makeText(context, getString(R.string.error_duplicate_date), Toast.LENGTH_SHORT).show()
                },
                onDateAvailable = {
                    updateRecord(weightText.toDouble())
                },
                onError = { error ->
                    setLoadingState(false)
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                }
            )
        }
        else {
            updateRecord(weightText.toDouble())
        }
    }

    private fun updateRecord(weight: Double) {
        val updatedRecord = currentRecord.copy(
            weight = weight,
            date = selectedDate.timeInMillis)
        listener?.onWeightRecordUpdate(updatedRecord)
        setLoadingState(false)
        dismiss()
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_record))
            .setMessage(getString(R.string.delete_confirmation))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                listener?.onWeightRecordDelete(currentRecord)
                dismiss()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()

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

    fun setWeightRecord(record: WeightRecord) {
        currentRecord = record
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}