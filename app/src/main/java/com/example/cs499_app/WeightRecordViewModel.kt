package com.example.cs499_app

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class WeightRecordViewModel(private val weightRecordRepository: WeightRecordRepository) : ViewModel() {
    // Insertion of weight record
    fun insertWeightRecord(weightRecord: WeightRecordEntity) {
        viewModelScope.launch {
            weightRecordRepository.insertWeightRecord(weightRecord) }
    }

    // Update weight record
    fun updateWeightRecord(weightRecord: WeightRecordEntity) {
        viewModelScope.launch {
            weightRecordRepository.updateWeightRecord(weightRecord) }
    }

    // Delete weight record
    fun deleteWeightRecord(weightRecord: WeightRecordEntity) {
        viewModelScope.launch {
            weightRecordRepository.deleteWeightRecord(weightRecord) }
    }

    // Get all records
    fun getWeightRecordsFlow(userId: Int): LiveData<List<WeightRecordEntity>> {
        return weightRecordRepository.getWeightRecordsFlow(userId).asLiveData()
    }
}