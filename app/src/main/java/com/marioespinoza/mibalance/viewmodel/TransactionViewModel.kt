package com.marioespinoza.mibalance.viewmodel

import androidx.lifecycle.ViewModel
import com.marioespinoza.mibalance.data.memory.MemoryDataRepository
import com.marioespinoza.mibalance.domain.model.Transaction
import com.marioespinoza.mibalance.domain.model.TransactionFilter
import com.marioespinoza.mibalance.domain.repository.TransactionRepository

class TransactionViewModel : ViewModel() {
    companion object {
        private val repository: TransactionRepository = MemoryDataRepository()

        fun getTransactions(filter: TransactionFilter): List<Transaction> = repository.find(filter)

        fun getTransaction(id: String): Transaction? = repository.findById(id)

        fun saveTransaction(transaction: Transaction) {
            repository.save(transaction)
        }

        fun removeTransaction(id: String) {
            repository.delete(id)
        }
    }
}
