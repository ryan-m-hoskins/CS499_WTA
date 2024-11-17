package com.example.cs499_app

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface WeightRecordDAO {
    @Insert
    suspend fun insert(weightRecordEntity: WeightRecordEntity)

    @Update
    suspend fun update(weightRecordEntity: WeightRecordEntity)

    @Delete
    suspend fun delete(weightRecordEntity: WeightRecordEntity)

    @Query("SELECT * FROM weight_records WHERE user_id = :userId")
    fun getWeightRecordsFlow(userId: Int): Flow<List<WeightRecordEntity>>
}