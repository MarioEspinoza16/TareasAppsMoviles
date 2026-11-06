package com.marioespinoza.mibalance.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.marioespinoza.mibalance.R
import com.marioespinoza.mibalance.domain.model.Category
import com.marioespinoza.mibalance.domain.model.Transaction
import com.marioespinoza.mibalance.domain.model.TransactionFilter
import com.marioespinoza.mibalance.domain.model.TransactionType
import com.marioespinoza.mibalance.util.labelRes
import com.marioespinoza.mibalance.util.monthText
import com.marioespinoza.mibalance.util.openDetail
import com.marioespinoza.mibalance.util.setupScreen
import com.marioespinoza.mibalance.viewmodel.TransactionViewModel
import java.time.YearMonth

class HistoryActivity : AppCompatActivity(), OnTransactionClickListener {
    private var month = YearMonth.now()
    private var type: TransactionType? = null
    private var category: Category? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        setupScreen()
        val savedMonth = savedInstanceState?.getString("month") ?: intent.getStringExtra("month")
        if (savedMonth != null) month = YearMonth.parse(savedMonth)
        type = savedInstanceState?.getString("type")?.let(TransactionType::valueOf)
        category = savedInstanceState?.getString("category")?.let(Category::valueOf)
        findViewById<RecyclerView>(R.id.transactions).layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.typeFilter).setOnClickListener {
            type = when (type) {
                null -> TransactionType.INCOME
                TransactionType.INCOME -> TransactionType.EXPENSE
                TransactionType.EXPENSE -> null
            }
            category = null
            load()
        }
        findViewById<Button>(R.id.categoryFilter).setOnClickListener {
            val categories = Category.entries.filter { type == null || it.type == type }
            category = categories.getOrNull(categories.indexOf(category) + 1)
            load()
        }
        findViewById<Button>(R.id.previousMonth).setOnClickListener {
            month = month.minusMonths(1)
            load()
        }
        findViewById<Button>(R.id.nextMonth).setOnClickListener {
            month = month.plusMonths(1)
            load()
        }
        findViewById<Button>(R.id.add).setOnClickListener {
            startActivity(Intent(this, TransactionEditActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        load()
    }

    private fun load() {
        val rows = TransactionViewModel.getTransactions(TransactionFilter(month, type, category))
        val typeLabel = when (type) {
            TransactionType.INCOME -> R.string.income
            TransactionType.EXPENSE -> R.string.expense
            null -> R.string.all_types
        }
        findViewById<TextView>(R.id.month).text = monthText(month)
        findViewById<Button>(R.id.typeFilter).setText(typeLabel)
        findViewById<Button>(R.id.categoryFilter).setText(category?.labelRes ?: R.string.all_categories)
        findViewById<TextView>(R.id.empty).visibility = if (rows.isEmpty()) View.VISIBLE else View.GONE
        findViewById<RecyclerView>(R.id.transactions).adapter = TransactionListAdapter(rows, this)
    }

    override fun onTransactionClick(transaction: Transaction) = openDetail(transaction)

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("month", month.toString())
        outState.putString("type", type?.name)
        outState.putString("category", category?.name)
        super.onSaveInstanceState(outState)
    }
}
