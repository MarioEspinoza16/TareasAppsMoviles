package com.marioespinoza.mibalance.data.memory

import com.marioespinoza.mibalance.domain.model.Transaction
import com.marioespinoza.mibalance.domain.repository.TransactionRepository

/** Volatile implementation required by task 3; useful for unit tests and prototypes. */
class MemoryDataRepository : TransactionRepository {
    private val items = linkedMapOf<String, Transaction>()
    @Synchronized override fun getAll(): List<Transaction> = items.values.sortedByDescending { it.date }
    @Synchronized override fun findById(id: String): Transaction? = items[id]
    @Synchronized override fun save(transaction: Transaction) { items[transaction.id] = transaction }
    @Synchronized override fun delete(id: String) { items.remove(id) }
}
