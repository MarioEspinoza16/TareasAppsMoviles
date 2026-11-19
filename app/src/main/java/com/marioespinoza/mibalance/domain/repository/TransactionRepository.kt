package com.marioespinoza.mibalance.domain.repository

import com.marioespinoza.mibalance.domain.model.Transaction
import com.marioespinoza.mibalance.domain.model.TransactionFilter

interface TransactionRepository {
    fun getAll(): List<Transaction>
    fun findById(id: String): Transaction?
    fun save(transaction: Transaction)
    fun delete(id: String)
    fun find(filter: TransactionFilter): List<Transaction> = getAll().filter(filter::matches)
    fun search(query: String): List<Transaction> {
        val text = query.trim()
        if (text.isBlank()) return emptyList()
        return getAll().filter {
            it.id.contains(text, ignoreCase = true) || it.description.contains(text, ignoreCase = true)
        }
    }
}
