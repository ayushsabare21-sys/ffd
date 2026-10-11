package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.SalaryCreditEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SalaryDao {
    @Query("SELECT * FROM salary_credits ORDER BY dateMillis DESC")
    fun getAllSalaryCredits(): Flow<List<SalaryCreditEntity>>

    @Query("SELECT COUNT(*) FROM salary_credits")
    fun getSalaryCount(): Flow<Int>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM salary_credits")
    fun getTotalSalaryAmount(): Flow<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSalary(salary: SalaryCreditEntity): Long

    @Delete
    suspend fun deleteSalary(salary: SalaryCreditEntity)

    @Query("DELETE FROM salary_credits WHERE id = :id")
    suspend fun deleteById(id: Long)
}
