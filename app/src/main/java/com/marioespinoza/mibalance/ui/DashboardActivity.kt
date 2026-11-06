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
import com.marioespinoza.mibalance.domain.model.MonthlySummary
import com.marioespinoza.mibalance.domain.model.Transaction
import com.marioespinoza.mibalance.domain.model.TransactionFilter
import com.marioespinoza.mibalance.domain.model.TransactionType
import com.marioespinoza.mibalance.util.labelRes
import com.marioespinoza.mibalance.util.money
import com.marioespinoza.mibalance.util.monthText
import com.marioespinoza.mibalance.util.openDetail
import com.marioespinoza.mibalance.util.setupScreen
import com.marioespinoza.mibalance.viewmodel.TransactionViewModel
import java.time.YearMonth

class DashboardActivity : AppCompatActivity(), OnTransactionClickListener {
    private var month = YearMonth.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        setupScreen(back = false)
        val savedMonth = savedInstanceState?.getString("month")
        if (savedMonth != null) month = YearMonth.parse(savedMonth)
        findViewById<RecyclerView>(R.id.recent).layoutManager = LinearLayoutManager(this)
        findViewById<Button>(R.id.previousMonth).setOnClickListener {
            month = month.minusMonths(1)
            load()
        }
        findViewById<Button>(R.id.nextMonth).setOnClickListener {
            month = month.plusMonths(1)
            load()
        }
        findViewById<Button>(R.id.addIncome).setOnClickListener { openForm(TransactionType.INCOME) }
        findViewById<Button>(R.id.addExpense).setOnClickListener { openForm(TransactionType.EXPENSE) }
        findViewById<Button>(R.id.history).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java).putExtra("month", month.toString()))
        }
    }

    override fun onResume() {
        super.onResume()
        load()
    }

    private fun load() {
        val rows = TransactionViewModel.getTransactions(TransactionFilter(month))
        val summary = MonthlySummary.from(rows)
        findViewById<TextView>(R.id.month).text = monthText(month)
        findViewById<TextView>(R.id.balance).text = money(summary.balanceMinor)
        findViewById<TextView>(R.id.income).text =
            getString(R.string.labeled_amount, getString(R.string.income_total), money(summary.incomeMinor))
        findViewById<TextView>(R.id.expenses).text =
            getString(R.string.labeled_amount, getString(R.string.expense_total), money(summary.expenseMinor))
        findViewById<TextView>(R.id.vehicle).text =
            getString(R.string.labeled_amount, getString(R.string.vehicle_total), money(summary.vehicleMinor))
        findViewById<TextView>(R.id.empty).visibility = if (rows.isEmpty()) View.VISIBLE else View.GONE
        findViewById<TextView>(R.id.categories).text = summary.expensesByCategory.entries.joinToString("\n") {
            getString(R.string.transaction_meta, getString(it.key.labelRes), money(it.value))
        }
        findViewById<RecyclerView>(R.id.recent).adapter = TransactionListAdapter(rows.take(4), this)
    }

    private fun openForm(type: TransactionType) {
        startActivity(Intent(this, TransactionEditActivity::class.java).putExtra("type", type.name))
    }

    override fun onTransactionClick(transaction: Transaction) = openDetail(transaction)

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("month", month.toString())
        super.onSaveInstanceState(outState)
    }
}
