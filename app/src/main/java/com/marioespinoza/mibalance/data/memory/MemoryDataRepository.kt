package com.marioespinoza.mibalance.data.memory

import com.marioespinoza.mibalance.domain.model.Transaction
import com.marioespinoza.mibalance.domain.repository.TransactionRepository

class MemoryDataRepository : TransactionRepository {
    private val items = mutableListOf<Transaction>()

    override fun getAll(): List<Transaction> = items.sortedByDescending { it.date }

    override fun findById(id: String): Transaction? = items.find { it.id == id }

    override fun save(transaction: Transaction) {
        delete(transaction.id)
        items.add(transaction)
    }

    override fun delete(id: String) {
        items.removeIf { it.id == id }
    }
}
