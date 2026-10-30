package com.marioespinoza.mibalance.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.marioespinoza.mibalance.R
import com.marioespinoza.mibalance.databinding.ActivityDashboardBinding
import com.marioespinoza.mibalance.domain.model.*
import com.marioespinoza.mibalance.util.labelRes
import com.marioespinoza.mibalance.viewmodel.DashboardViewModel
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class DashboardActivity : BaseActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var model: DashboardViewModel
    private var month = YearMonth.now()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        month = savedInstanceState?.getString("month")?.let(YearMonth::parse) ?: month
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root); setup(binding.root, binding.toolbar, false)
        model = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application))[DashboardViewModel::class.java]
        binding.previousMonth.setOnClickListener { month = month.minusMonths(1); load() }
        binding.nextMonth.setOnClickListener { month = month.plusMonths(1); load() }
        binding.addIncome.setOnClickListener { create(TransactionType.INCOME) }
        binding.addExpense.setOnClickListener { create(TransactionType.EXPENSE) }
        binding.history.setOnClickListener { startActivity(Intent(this, HistoryActivity::class.java).putExtra("month", month.toString())) }
        binding.language.setOnClickListener {
            MaterialAlertDialogBuilder(this).setTitle(R.string.language)
                .setItems(arrayOf(getString(R.string.english), getString(R.string.spanish))) { _, index ->
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(if (index == 0) "en" else "es"))
                }.show()
        }
        model.state.observe(this) { result ->
            result.onSuccess { rows ->
                val summary = MonthlySummary.from(rows)
                binding.balance.text = money(summary.balanceMinor)
                binding.income.text = getString(R.string.labeled_amount, getString(R.string.income_total), money(summary.incomeMinor))
                binding.expenses.text = getString(R.string.labeled_amount, getString(R.string.expense_total), money(summary.expenseMinor))
                binding.vehicle.text = getString(R.string.labeled_amount, getString(R.string.vehicle_total), money(summary.vehicleMinor))
                binding.empty.visibility = if (rows.isEmpty()) View.VISIBLE else View.GONE
                binding.categories.removeAllViews()
                summary.expensesByCategory.entries.sortedByDescending { it.value }.forEach { (category, value) ->
                    binding.categories.addView(TextView(this).apply {
                        text = getString(R.string.transaction_meta, getString(category.labelRes), money(value))
                        textSize = 16f; setTextColor(getColor(R.color.ink)); setPadding(0, 20, 0, 16)
                    })
                }
                addRows(binding.recent, rows.take(4))
            }.onFailure { toast(R.string.error_load); binding.balance.setText(R.string.error_load) }
        }
    }
    override fun onResume() { super.onResume(); load() }
    private fun load() {
        binding.month.text = month.format(DateTimeFormatter.ofPattern("MMMM yyyy", resources.configuration.locales[0]))
        model.load(TransactionFilter(month))
    }
    private fun create(type: TransactionType) { startActivity(Intent(this, TransactionEditActivity::class.java).putExtra("type", type.name)) }
    override fun onSaveInstanceState(outState: Bundle) { outState.putString("month", month.toString()); super.onSaveInstanceState(outState) }
}
