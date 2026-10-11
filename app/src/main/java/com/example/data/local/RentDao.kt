package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.RentPaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RentDao {
    @Query("SELECT * FROM rent_payments ORDER BY dateMillis DESC")
    fun getAllRentPayments(): Flow<List<RentPaymentEntity>>

    @Query("SELECT COUNT(*) FROM rent_payments")
    fun getRentPaidCount(): Flow<Int>

    @Query("SELECT COALESCE(SUM(myShare), 0) FROM rent_payments")
    fun getTotalMyShareRentPaid(): Flow<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRentPayment(rent: RentPaymentEntity): Long

    @Delete
    suspend fun deleteRentPayment(rent: RentPaymentEntity)

    @Query("DELETE FROM rent_payments WHERE id = :id")
    suspend fun deleteById(id: Long)
}
