package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.ExpenseEntity
import com.example.data.model.RentPaymentEntity
import com.example.data.model.SalaryCreditEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

@Database(
    entities = [
        ExpenseEntity::class,
        SalaryCreditEntity::class,
        RentPaymentEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PaisaWiseDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun salaryDao(): SalaryDao
    abstract fun rentDao(): RentDao

    companion object {
        @Volatile
        private var INSTANCE: PaisaWiseDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): PaisaWiseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PaisaWiseDatabase::class.java,
                    "paisawise_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }

            private suspend fun populateInitialData(database: PaisaWiseDatabase) {
                val now = System.currentTimeMillis()
                val cal = Calendar.getInstance()

                // Initial Salary Credit (1st month internship stipend)
                database.salaryDao().insertSalary(
                    SalaryCreditEntity(
                        amount = 15000.0,
                        dateMillis = now - (10L * 24 * 60 * 60 * 1000), // 10 days ago
                        companyOrSource = "Internship Stipend",
                        monthLabel = "Current Month",
                        note = "Monthly internship stipend ₹15,000 credited"
                    )
                )

                // Initial Room Rent payment (Paid to owner, 4000 split by 2 = 2000 share)
                database.rentDao().insertRentPayment(
                    RentPaymentEntity(
                        totalRent = 4000.0,
                        myShare = 2000.0,
                        roommateShare = 2000.0,
                        dateMillis = now - (9L * 24 * 60 * 60 * 1000),
                        monthLabel = "Current Month",
                        ownerName = "Room Owner (Sharma Ji)",
                        paymentMode = "UPI (GPay)",
                        isConfirmedPaid = true,
                        receiptNote = "Month room rent ₹4000 (My share: ₹2000, Roommate: ₹2000)"
                    )
                )

                // Log Room Rent as an expense item for my share
                database.expenseDao().insertExpense(
                    ExpenseEntity(
                        title = "Room Rent (My Share)",
                        amount = 2000.0,
                        category = "ROOM_RENT",
                        dateMillis = now - (9L * 24 * 60 * 60 * 1000),
                        note = "₹4000 split with roommate"
                    )
                )

                // Mobile Recharge
                database.expenseDao().insertExpense(
                    ExpenseEntity(
                        title = "Monthly Mobile Recharge",
                        amount = 400.0,
                        category = "MOBILE_RECHARGE",
                        dateMillis = now - (8L * 24 * 60 * 60 * 1000),
                        note = "28-day 1.5GB/day recharge"
                    )
                )

                // Light Bill (user portion or shared payment)
                database.expenseDao().insertExpense(
                    ExpenseEntity(
                        title = "Electricity / Light Bill",
                        amount = 750.0,
                        category = "LIGHT_BILL",
                        dateMillis = now - (6L * 24 * 60 * 60 * 1000),
                        note = "₹1500 split with roommate (my share: ₹750)"
                    )
                )

                // Daily Food Examples (Morning ₹70, Night ₹35)
                database.expenseDao().insertExpense(
                    ExpenseEntity(
                        title = "Morning Breakfast & Tiffin",
                        amount = 70.0,
                        category = "FOOD",
                        dateMillis = now - (1L * 24 * 60 * 60 * 1000),
                        isPresetFood = true,
                        note = "Poha + Chai + Morning tiffin"
                    )
                )
                database.expenseDao().insertExpense(
                    ExpenseEntity(
                        title = "Night Dinner",
                        amount = 35.0,
                        category = "FOOD",
                        dateMillis = now - (1L * 24 * 60 * 60 * 1000),
                        isPresetFood = true,
                        note = "Mess night meal"
                    )
                )

                // Work Porter delivery (reimbursable)
                database.expenseDao().insertExpense(
                    ExpenseEntity(
                        title = "Porter Courier for Office Samples",
                        amount = 180.0,
                        category = "WORK_PORTER",
                        dateMillis = now - (2L * 24 * 60 * 60 * 1000),
                        isReimbursable = true,
                        isReimbursed = false,
                        note = "Courier sent from office to client. Receipt saved for reimbursement."
                    )
                )

                // Transport
                database.expenseDao().insertExpense(
                    ExpenseEntity(
                        title = "Bus Pass / Metro Commute",
                        amount = 250.0,
                        category = "TRANSPORT",
                        dateMillis = now - (4L * 24 * 60 * 60 * 1000),
                        note = "Weekly commute to internship office"
                    )
                )
            }
        }
    }
}
