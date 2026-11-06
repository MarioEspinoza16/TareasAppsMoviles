package com.marioespinoza.mibalance.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.marioespinoza.mibalance.R
import com.marioespinoza.mibalance.domain.model.Category
import com.marioespinoza.mibalance.domain.model.Transaction
import com.marioespinoza.mibalance.domain.model.TransactionType
import com.marioespinoza.mibalance.util.Money
import com.marioespinoza.mibalance.util.dateText
import com.marioespinoza.mibalance.util.labelRes
import com.marioespinoza.mibalance.util.setupScreen
import com.marioespinoza.mibalance.util.toast
import com.marioespinoza.mibalance.viewmodel.TransactionViewModel
import java.time.LocalDate

class TransactionEditActivity : AppCompatActivity() {
    private var type = TransactionType.EXPENSE
    private var category = Category.FUEL
    private var date = LocalDate.now()
    private var existing: Transaction? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_edit)
        setupScreen()

        val id = intent.getStringExtra("transaction_id")
        existing = id?.let { TransactionViewModel.getTransaction(it) }
        if (id != null && existing == null) {
            toast(R.string.error_not_found)
            finish()
            return
        }
        val transaction = existing
        val savedType = savedInstanceState?.getString("type") ?: transaction?.type?.name ?: intent.getStringExtra("type")
        if (savedType != null) type = TransactionType.valueOf(savedType)
        val savedCategory = savedInstanceState?.getString("category")
        category = savedCategory?.let(Category::valueOf) ?: transaction?.category ?: categories().first()
        val savedDate = savedInstanceState?.getString("date")
        date = savedDate?.let(LocalDate::parse) ?: transaction?.date ?: date
        if (transaction != null) {
            findViewById<MaterialToolbar>(R.id.toolbar).setTitle(R.string.edit_transaction)
            if (savedInstanceState == null) {
                findViewById<EditText>(R.id.amount).setText(Money.input(transaction.amountMinor))
                findViewById<EditText>(R.id.description).setText(transaction.description)
                findViewById<EditText>(R.id.odometer).setText(transaction.odometerKm?.toString().orEmpty())
                findViewById<EditText>(R.id.liters).setText(transaction.fuelLiters.orEmpty())
            }
        }

        findViewById<Button>(R.id.type).setOnClickListener {
            type = if (type == TransactionType.INCOME) TransactionType.EXPENSE else TransactionType.INCOME
            category = categories().first()
            showSelection()
        }
        findViewById<Button>(R.id.category).setOnClickListener {
            val choices = categories()
            category = choices[(choices.indexOf(category) + 1) % choices.size]
            showSelection()
        }
        findViewById<Button>(R.id.date).setOnClickListener {
            DatePickerDialog(this, { _, year, month, day ->
                date = LocalDate.of(year, month + 1, day)
                showDate()
            }, date.year, date.monthValue - 1, date.dayOfMonth).show()
        }
        findViewById<Button>(R.id.save).setOnClickListener { save() }
        findViewById<Button>(R.id.cancel).setOnClickListener { finish() }
        showSelection()
        showDate()
    }

    private fun categories() = Category.entries.filter { it.type == type }

    private fun showSelection() {
        findViewById<Button>(R.id.type).setText(if (type == TransactionType.INCOME) R.string.income else R.string.expense)
        findViewById<Button>(R.id.category).setText(category.labelRes)
        val descriptionHint = when (category) {
            Category.MAINTENANCE -> R.string.maintenance_detail
            Category.REPAIRS -> R.string.repair_detail
            else -> R.string.description
        }
        findViewById<EditText>(R.id.description).setHint(descriptionHint)
        findViewById<EditText>(R.id.odometer).visibility = if (category.isVehicle) View.VISIBLE else View.GONE
        findViewById<EditText>(R.id.liters).visibility = if (category == Category.FUEL) View.VISIBLE else View.GONE
    }

    private fun showDate() {
        findViewById<Button>(R.id.date).text = getString(R.string.date_button, dateText(date))
    }

    private fun save() {
        val amountField = findViewById<EditText>(R.id.amount)
        val amount = Money.parseMinor(amountField.text.toString())
        if (amount == null) {
            amountField.error = getString(R.string.error_amount)
            return
        }
        val descriptionField = findViewById<EditText>(R.id.description)
        val description = descriptionField.text.toString().trim()
        if ((category == Category.MAINTENANCE || category == Category.REPAIRS) && description.isBlank()) {
            descriptionField.error = getString(R.string.error_description)
            return
        }

        val odometerField = findViewById<EditText>(R.id.odometer)
        val odometerText = if (category.isVehicle) odometerField.text.toString().trim() else ""
        val odometer = odometerText.toLongOrNull()
        if (odometerText.isNotEmpty() && (odometer == null || odometer < 0)) {
            odometerField.error = getString(R.string.error_number)
            return
        }

        val litersField = findViewById<EditText>(R.id.liters)
        val liters = if (category == Category.FUEL) litersField.text.toString().trim().replace(',', '.') else ""
        val litersValue = liters.toDoubleOrNull()
        if (liters.isNotEmpty() && (litersValue == null || !litersValue.isFinite() || litersValue <= 0)) {
            litersField.error = getString(R.string.error_number)
            return
        }

        var transaction = Transaction(
            type = type, amountMinor = amount, date = date, category = category,
            description = description, odometerKm = odometer, fuelLiters = liters.ifBlank { null }
        )
        val previous = existing
        if (previous != null) transaction = transaction.copy(id = previous.id)
        TransactionViewModel.saveTransaction(transaction)
        toast(R.string.saved)
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("type", type.name)
        outState.putString("category", category.name)
        outState.putString("date", date.toString())
        super.onSaveInstanceState(outState)
    }
}
