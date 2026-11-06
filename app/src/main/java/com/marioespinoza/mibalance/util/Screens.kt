package com.marioespinoza.mibalance.util

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.marioespinoza.mibalance.R
import com.marioespinoza.mibalance.domain.model.Transaction
import com.marioespinoza.mibalance.ui.DetailActivity
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

fun AppCompatActivity.setupScreen(back: Boolean = true) {
    val root = findViewById<View>(R.id.root)
    ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
        insets
    }
    if (back) {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
        toolbar.navigationContentDescription = getString(R.string.back)
        toolbar.setNavigationOnClickListener { finish() }
    }
}

fun AppCompatActivity.toast(message: Int) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.openDetail(transaction: Transaction) {
    startActivity(Intent(this, DetailActivity::class.java).putExtra("transaction_id", transaction.id))
}

fun AppCompatActivity.money(amount: Long): String =
    Money.format(amount, resources.configuration.locales[0])

fun AppCompatActivity.dateText(date: LocalDate): String =
    date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(resources.configuration.locales[0]))

fun AppCompatActivity.monthText(month: YearMonth): String =
    month.format(DateTimeFormatter.ofPattern("MMMM yyyy", resources.configuration.locales[0]))
