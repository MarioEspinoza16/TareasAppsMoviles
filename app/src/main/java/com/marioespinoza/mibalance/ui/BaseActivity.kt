package com.marioespinoza.mibalance.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.marioespinoza.mibalance.R
import com.marioespinoza.mibalance.databinding.ItemTransactionBinding
import com.marioespinoza.mibalance.domain.model.Transaction
import com.marioespinoza.mibalance.domain.model.TransactionType
import com.marioespinoza.mibalance.util.Money
import com.marioespinoza.mibalance.util.labelRes
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

abstract class BaseActivity : AppCompatActivity() {
    protected fun setup(root: View, toolbar: MaterialToolbar, back: Boolean = true) {
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        ViewCompat.requestApplyInsets(root)
        if (back) {
            toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
            toolbar.navigationContentDescription = getString(R.string.back)
            toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        }
    }
    protected fun toast(message: Int) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    protected fun dateText(date: LocalDate): String = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(resources.configuration.locales[0]))
    protected fun money(value: Long): String = Money.format(value, resources.configuration.locales[0])
    protected fun fillSpinner(spinner: Spinner, labels: List<String>, selected: Int = 0) {
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, labels).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinner.setSelection(selected.coerceIn(0, labels.lastIndex.coerceAtLeast(0)))
    }
    protected fun addRows(container: LinearLayout, rows: List<Transaction>) {
        container.removeAllViews()
        rows.forEach { item ->
            val row = ItemTransactionBinding.inflate(layoutInflater, container, false)
            val isIncome = item.type == TransactionType.INCOME
            row.category.setText(item.category.labelRes)
            row.amount.text = getString(if (isIncome) R.string.signed_income_amount else R.string.signed_expense_amount, money(item.amountMinor))
            row.amount.setTextColor(getColor(if (isIncome) R.color.income else R.color.expense))
            row.meta.text = getString(R.string.transaction_meta, getString(if (isIncome) R.string.income else R.string.expense), dateText(item.date))
            row.description.text = item.description
            row.description.visibility = if (item.description.isBlank()) View.GONE else View.VISIBLE
            row.root.contentDescription = getString(R.string.view_transaction, row.category.text.toString() + ", " + row.amount.text + ", " + row.meta.text)
            row.root.setOnClickListener { startActivity(Intent(this, DetailActivity::class.java).putExtra("transaction_id", item.id)) }
            container.addView(row.root)
        }
    }
}
