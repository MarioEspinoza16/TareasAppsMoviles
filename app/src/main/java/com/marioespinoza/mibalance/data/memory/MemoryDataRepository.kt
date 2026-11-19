package com.marioespinoza.mibalance.data.memory

import com.marioespinoza.mibalance.domain.model.Transaction
import com.marioespinoza.mibalance.domain.repository.TransactionRepository

class MemoryDataRepository : TransactionRepository {
    private val items = mutableListOf<Transaction>()
    private var nextId = 1L

    override fun getAll(): List<Transaction> = items.sortedByDescending { it.date }

    override fun findById(id: String): Transaction? = items.find { it.id == id }

    override fun save(transaction: Transaction): Transaction {
        val saved = if (transaction.id.isBlank()) {
            transaction.copy(id = (nextId++).toString())
        } else {
            require(items.any { it.id == transaction.id }) { "Transaction not found" }
            transaction
        }
        delete(saved.id)
        items.add(saved)
        return saved
    }

    override fun delete(id: String) {
        items.removeIf { it.id == id }
    }
}
