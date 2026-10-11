package com.example.data.repository

import com.example.data.local.ExpenseDao
import com.example.data.local.RentDao
import com.example.data.local.SalaryDao
import com.example.data.model.ExpenseEntity
import com.example.data.model.RentPaymentEntity
import com.example.data.model.SalaryCreditEntity
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(
    private val expenseDao: ExpenseDao,
    private val salaryDao: SalaryDao,
    private val rentDao: RentDao
) {
    val allExpenses: Flow<List<ExpenseEntity>> = expenseDao.getAllExpenses()
    val reimbursableExpenses: Flow<List<ExpenseEntity>> = expenseDao.getReimbursableExpenses()
    
    val allSalaryCredits: Flow<List<SalaryCreditEntity>> = salaryDao.getAllSalaryCredits()
    val salaryCount: Flow<Int> = salaryDao.getSalaryCount()
    val totalSalary: Flow<Double> = salaryDao.getTotalSalaryAmount()

    val allRentPayments: Flow<List<RentPaymentEntity>> = rentDao.getAllRentPayments()
    val rentPaidCount: Flow<Int> = rentDao.getRentPaidCount()
    val totalMyShareRentPaid: Flow<Double> = rentDao.getTotalMyShareRentPaid()

    fun getExpensesForDay(startOfDay: Long, endOfDay: Long): Flow<List<ExpenseEntity>> =
        expenseDao.getExpensesForDay(startOfDay, endOfDay)

    fun getExpensesForMonth(startOfMonth: Long, endOfMonth: Long): Flow<List<ExpenseEntity>> =
        expenseDao.getExpensesForMonth(startOfMonth, endOfMonth)

    suspend fun insertExpense(expense: ExpenseEntity): Long =
        expenseDao.insertExpense(expense)

    suspend fun updateExpense(expense: ExpenseEntity) =
        expenseDao.updateExpense(expense)

    suspend fun deleteExpense(expense: ExpenseEntity) =
        expenseDao.deleteExpense(expense)

    suspend fun deleteExpenseById(id: Long) =
        expenseDao.deleteById(id)

    suspend fun insertSalary(salary: SalaryCreditEntity): Long =
        salaryDao.insertSalary(salary)

    suspend fun deleteSalaryById(id: Long) =
        salaryDao.deleteById(id)

    suspend fun insertRentPayment(rent: RentPaymentEntity): Long =
        rentDao.insertRentPayment(rent)

    suspend fun deleteRentPaymentById(id: Long) =
        rentDao.deleteById(id)
}
