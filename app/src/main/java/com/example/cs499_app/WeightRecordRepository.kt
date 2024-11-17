package com.example.cs499_app

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


// Removed constructor as Kotlin will generate it in class header, gets initialized with valid non-null DAO
class WeightRecordRepository(private val weightRecordDao: WeightRecordDAO) {

    // Methods to interact with DAO
    suspend fun insertWeightRecord(weightRecordEntity: WeightRecordEntity) {
        withContext(Dispatchers.IO) {
            weightRecordDao.insert(weightRecordEntity)
        }
    }

    suspend fun updateWeightRecord(weightRecordEntity: WeightRecordEntity) {
        weightRecordDao.update(weightRecordEntity)
    }

    suspend fun deleteWeightRecord(weightRecordEntity: WeightRecordEntity) {
        weightRecordDao.delete(weightRecordEntity)
    }

    fun getWeightRecordsFlow(userId: Int): Flow<List<WeightRecordEntity>> {
        return weightRecordDao.getWeightRecordsFlow(userId)
    }
    // == Backup method == //
    /*
        fun getWeightRecordsFlow(userId: Int): Flow<List<WeightRecordEntity?>> {
        return weightRecordDao.getWeightRecordsFlow(userId)
            .filterNotNull();
    }
     */
}