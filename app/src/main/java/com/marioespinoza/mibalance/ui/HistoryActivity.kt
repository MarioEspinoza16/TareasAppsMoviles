package com.marioespinoza.mibalance.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.ViewModelProvider
import com.marioespinoza.mibalance.R
import com.marioespinoza.mibalance.databinding.ActivityHistoryBinding
import com.marioespinoza.mibalance.domain.model.*
import com.marioespinoza.mibalance.util.labelRes
import com.marioespinoza.mibalance.viewmodel.HistoryViewModel
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class HistoryActivity : BaseActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var model: HistoryViewModel
    private var month = YearMonth.now()
    private var type: TransactionType? = null
    private var category: Category? = null
    private var categories = emptyList<Category>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        month = (savedInstanceState?.getString("month") ?: intent.getStringExtra("month"))?.let(YearMonth::parse) ?: month
        type = savedInstanceState?.getString("type")?.let(TransactionType::valueOf)
        category = savedInstanceState?.getString("category")?.let(Category::valueOf)
        binding = ActivityHistoryBinding.inflate(layoutInflater); setContentView(binding.root); setup(binding.root, binding.toolbar)
        model = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application))[HistoryViewModel::class.java]
        fillSpinner(binding.typeFilter, listOf(getString(R.string.all_types), getString(R.string.income), getString(R.string.expense)), when(type) { TransactionType.INCOME -> 1; TransactionType.EXPENSE -> 2; else -> 0 })
        updateCategories()
        binding.typeFilter.onItemSelectedListener = listener { index ->
            val next = when(index) { 1 -> TransactionType.INCOME; 2 -> TransactionType.EXPENSE; else -> null }
            if (next != type) { type = next; if (category?.type != next && next != null) category = null; updateCategories() }
            load()
        }
        binding.categoryFilter.onItemSelectedListener = listener { index -> category = categories.getOrNull(index - 1); load() }
        binding.previousMonth.setOnClickListener { month = month.minusMonths(1); load() }
        binding.nextMonth.setOnClickListener { month = month.plusMonths(1); load() }
        binding.add.setOnClickListener { startActivity(Intent(this, TransactionEditActivity::class.java)) }
        model.state.observe(this) { result ->
            result.onSuccess { rows ->
                binding.empty.setText(R.string.empty_filter)
                binding.empty.visibility = if (rows.isEmpty()) View.VISIBLE else View.GONE
                addRows(binding.transactions, rows)
            }.onFailure { binding.transactions.removeAllViews(); binding.empty.visibility = View.VISIBLE; binding.empty.setText(R.string.error_load) }
        }
    }
    private fun updateCategories() {
        categories = Category.entries.filter { type == null || it.type == type }
        fillSpinner(binding.categoryFilter, listOf(getString(R.string.all_categories)) + categories.map { getString(it.labelRes) }, categories.indexOf(category) + 1)
    }
    private fun listener(action: (Int) -> Unit) = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) = action(position)
        override fun onNothingSelected(parent: AdapterView<*>?) = Unit
    }
    private fun load() {
        binding.month.text = month.format(DateTimeFormatter.ofPattern("MMMM yyyy", resources.configuration.locales[0]))
        model.load(TransactionFilter(month, type, category))
    }
    override fun onResume() { super.onResume(); load() }
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("month", month.toString()); outState.putString("type", type?.name); outState.putString("category", category?.name)
        super.onSaveInstanceState(outState)
    }
}
