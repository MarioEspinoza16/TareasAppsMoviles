package com.marioespinoza.mibalance

import android.app.Application
import com.marioespinoza.mibalance.data.local.LocalTransactionRepository
import com.marioespinoza.mibalance.domain.repository.TransactionRepository

class BalanceApplication : Application() {
    val repository: TransactionRepository by lazy { LocalTransactionRepository(this) }
}
