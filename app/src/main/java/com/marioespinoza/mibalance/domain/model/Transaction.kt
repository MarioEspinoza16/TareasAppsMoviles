package com.marioespinoza.mibalance.domain.model

import java.time.LocalDate
import java.time.YearMonth
import java.util.UUID

enum class TransactionType { INCOME, EXPENSE }
enum class Category(val type: TransactionType, val isVehicle: Boolean = false) {
    DRIVING(TransactionType.INCOME), OTHER_WORK(TransactionType.INCOME), OTHER_INCOME(TransactionType.INCOME),
    FUEL(TransactionType.EXPENSE, true), REPAIRS(TransactionType.EXPENSE, true), MAINTENANCE(TransactionType.EXPENSE, true),
    FOOD(TransactionType.EXPENSE), LEISURE(TransactionType.EXPENSE), PERSONAL(TransactionType.EXPENSE), OTHER_EXPENSE(TransactionType.EXPENSE)
}

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val type: TransactionType,
    val amountMinor: Long,
    val date: LocalDate,
    val category: Category,
    val description: String = "",
    val odometerKm: Long? = null,
    val fuelLiters: String? = null
) {
    init {
        require(amountMinor in 1..999_999_999_999L)
        require(category.type == type)
        require(category !in setOf(Category.MAINTENANCE, Category.REPAIRS) || description.isNotBlank())
        require(odometerKm == null || odometerKm >= 0)
        require(fuelLiters == null || (category == Category.FUEL && (fuelLiters.toDoubleOrNull() ?: 0.0) > 0.0))
    }
}

data class TransactionFilter(val month: YearMonth, val type: TransactionType? = null, val category: Category? = null) {
    fun matches(item: Transaction) = YearMonth.from(item.date) == month &&
        (type == null || item.type == type) && (category == null || item.category == category)
}

data class MonthlySummary(val incomeMinor: Long, val expenseMinor: Long, val vehicleMinor: Long, val expensesByCategory: Map<Category, Long>) {
    val balanceMinor: Long get() = incomeMinor - expenseMinor
    companion object {
        fun from(items: List<Transaction>): MonthlySummary {
            val expenses = items.filter { it.type == TransactionType.EXPENSE }
            return MonthlySummary(
                items.filter { it.type == TransactionType.INCOME }.sumOf { it.amountMinor },
                expenses.sumOf { it.amountMinor },
                expenses.filter { it.category.isVehicle }.sumOf { it.amountMinor },
                expenses.groupBy { it.category }.mapValues { (_, rows) -> rows.sumOf { it.amountMinor } }
            )
        }
    }
}
