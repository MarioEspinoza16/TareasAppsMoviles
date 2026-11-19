package com.marioespinoza.mibalance

import com.marioespinoza.mibalance.data.memory.MemoryDataRepository
import com.marioespinoza.mibalance.domain.model.*
import com.marioespinoza.mibalance.util.Money
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class FinanceTest {
    private fun expense(amount: Long, category: Category = Category.FUEL, date: LocalDate = LocalDate.of(2026, 10, 30)) =
        Transaction(type = TransactionType.EXPENSE, amountMinor = amount, date = date, category = category, description = "Oil change")
    @Test fun decimalAmountsAreExact() {
        assertEquals(1250050L, Money.parseMinor("12500,50"))
        assertEquals(1250050L, Money.parseMinor("12500.50"))
        assertEquals(1L, Money.parseMinor("0.01"))
        assertEquals(999999999999L, Money.parseMinor("9999999999.99"))
    }
    @Test fun invalidOrAmbiguousAmountsAreRejected() {
        listOf("", "0", "-1", "1.234", "1,234.56", "1 000", "NaN", "1e3", "10000000000").forEach { assertNull(it, Money.parseMinor(it)) }
    }
    @Test fun vehicleExpensesAreNotCountedTwice() {
        val rows = listOf(Transaction(type = TransactionType.INCOME, amountMinor = 10000000, date = LocalDate.of(2026, 10, 30), category = Category.DRIVING), expense(2000000), expense(1000000, Category.FOOD), expense(500000, Category.MAINTENANCE))
        val summary = MonthlySummary.from(rows)
        assertEquals(10000000L, summary.incomeMinor)
        assertEquals(3500000L, summary.expenseMinor)
        assertEquals(2500000L, summary.vehicleMinor)
        assertEquals(6500000L, summary.balanceMinor)
        assertEquals(summary.expenseMinor, summary.expensesByCategory.values.sum())
    }
    @Test fun negativeBalanceIsSupported() { assertEquals(-100L, MonthlySummary.from(listOf(expense(100))).balanceMinor) }
    @Test fun emptySummaryIsZero() { assertEquals(0L, MonthlySummary.from(emptyList()).balanceMinor) }
    @Test fun filtersRespectMonthYearTypeAndCategory() {
        val repository = MemoryDataRepository()
        val october = repository.save(expense(100))
        repository.save(expense(200, date = LocalDate.of(2025, 10, 1))); repository.save(expense(300, Category.FOOD))
        assertEquals(listOf(october), repository.find(TransactionFilter(YearMonth.of(2026, 10), TransactionType.EXPENSE, Category.FUEL)))
        assertTrue(repository.find(TransactionFilter(YearMonth.of(2026, 11))).isEmpty())
    }
    @Test fun updateDoesNotDuplicateAndDeleteRemovesRecord() {
        val repository = MemoryDataRepository(); val row = repository.save(expense(100))
        repository.save(row.copy(amountMinor = 200))
        assertEquals(1, repository.getAll().size); assertEquals(200L, repository.findById(row.id)?.amountMinor)
        repository.delete(row.id); assertNull(repository.findById(row.id))
    }
    @Test fun idsAreSequentialSharedAcrossTypesStableOnUpdateAndNeverReusedAfterDeletion() {
        val repository = MemoryDataRepository()
        val first = repository.save(expense(100))
        val second = repository.save(Transaction(type = TransactionType.INCOME, amountMinor = 200,
            date = LocalDate.now(), category = Category.DRIVING, description = "Driving shift"))
        val third = repository.save(expense(300))
        assertEquals(listOf("1", "2", "3"), listOf(first.id, second.id, third.id))
        repository.delete(second.id)
        val updated = repository.save(first.copy(amountMinor = 500))
        val fourth = repository.save(expense(400))
        assertEquals("1", updated.id)
        assertEquals("4", fourth.id)
        assertEquals(3, repository.getAll().size)
        assertEquals(500L, repository.findById("1")?.amountMinor)
        assertNull(repository.findById("2"))
    }
    @Test fun idLookupIsExactAndIncludesAllDates() {
        val repository = MemoryDataRepository()
        repeat(50) { repository.save(expense(100, date = LocalDate.of(2025, 1, 1))) }
        assertEquals("1", repository.findById("1")?.id)
        assertEquals("10", repository.findById("10")?.id)
        assertEquals(50, repository.getAll().size)
        assertNull(repository.findById("51"))
    }
    @Test(expected = IllegalArgumentException::class) fun categoryMustMatchType() {
        Transaction(type = TransactionType.INCOME, amountMinor = 100, date = LocalDate.now(), category = Category.FUEL)
    }
    @Test(expected = IllegalArgumentException::class) fun maintenanceNeedsDescription() {
        Transaction(type = TransactionType.EXPENSE, amountMinor = 100, date = LocalDate.now(), category = Category.MAINTENANCE)
    }
}
