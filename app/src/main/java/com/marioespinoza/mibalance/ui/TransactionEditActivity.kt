package com.marioespinoza.mibalance.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.marioespinoza.mibalance.R
import com.marioespinoza.mibalance.domain.model.Category
import com.marioespinoza.mibalance.domain.model.Transaction
import com.marioespinoza.mibalance.domain.model.TransactionType
import com.marioespinoza.mibalance.util.Money
import com.marioespinoza.mibalance.util.confirmAction
import com.marioespinoza.mibalance.util.dateText
import com.marioespinoza.mibalance.util.labelRes
import com.marioespinoza.mibalance.util.money
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

        val id = if (savedInstanceState != null) {
            savedInstanceState.getString("editing_id")
        } else {
            intent.getStringExtra("transaction_id")
        }
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
        if (transaction != null && savedInstanceState == null) {
            fillFields(transaction)
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
        findViewById<Button>(R.id.search).setOnClickListener { searchTransactions() }
        findViewById<Button>(R.id.newTransaction).setOnClickListener { clearForm() }
        findViewById<Button>(R.id.delete).setOnClickListener { deleteTransaction() }
        findViewById<Button>(R.id.cancel).setOnClickListener { finish() }
        showMode()
        showSelection()
        showDate()
    }

    private fun categories() = Category.entries.filter { it.type == type }

    private fun fillFields(transaction: Transaction) {
        existing = transaction
        type = transaction.type
        category = transaction.category
        date = transaction.date
        findViewById<EditText>(R.id.amount).setText(Money.input(transaction.amountMinor))
        findViewById<EditText>(R.id.description).setText(transaction.description)
        findViewById<EditText>(R.id.odometer).setText(transaction.odometerKm?.toString().orEmpty())
        findViewById<EditText>(R.id.liters).setText(transaction.fuelLiters.orEmpty())
        clearErrors()
        showMode()
        showSelection()
        showDate()
    }

    private fun showMode() {
        val transaction = existing
        findViewById<MaterialToolbar>(R.id.toolbar).setTitle(
            if (transaction == null) R.string.new_transaction else R.string.edit_transaction
        )
        findViewById<TextView>(R.id.transactionId).text = transaction?.let {
            getString(R.string.transaction_id_value, it.id)
        } ?: getString(R.string.new_transaction_hint)
        findViewById<Button>(R.id.save).setText(
            if (transaction == null) R.string.save else R.string.update
        )
        findViewById<Button>(R.id.delete).visibility = if (transaction == null) View.GONE else View.VISIBLE
    }

    private fun clearErrors() {
        listOf(R.id.amount, R.id.description, R.id.odometer, R.id.liters, R.id.searchQuery).forEach {
            findViewById<EditText>(it).error = null
        }
    }

    private fun clearForm() {
        existing = null
        type = TransactionType.EXPENSE
        category = Category.FUEL
        date = LocalDate.now()
        listOf(R.id.amount, R.id.description, R.id.odometer, R.id.liters, R.id.searchQuery).forEach {
            findViewById<EditText>(it).setText("")
        }
        clearErrors()
        showMode()
        showSelection()
        showDate()
    }

    private fun searchTransactions() {
        val queryField = findViewById<EditText>(R.id.searchQuery)
        val query = queryField.text.toString().trim()
        if (query.isBlank()) {
            queryField.error = getString(R.string.error_search)
            return
        }
        queryField.error = null
        val matches = TransactionViewModel.searchTransactions(query)
        if (matches.isEmpty()) {
            toast(R.string.error_not_found)
            return
        }
        val labels = matches.map {
            getString(R.string.search_result, dateText(it.date), money(it.amountMinor),
                getString(it.category.labelRes), it.description.ifBlank { getString(R.string.no_description) }, it.id)
        }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle(R.string.select_transaction)
            .setItems(labels) { _, position -> fillFields(matches[position]) }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun deleteTransaction() {
        val transaction = existing ?: return
        confirmAction(R.string.confirm_delete, R.string.delete_question, R.string.delete) {
            TransactionViewModel.removeTransaction(transaction.id)
            clearForm()
            toast(R.string.deleted)
        }
    }

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
        val transactionToSave = transaction
        if (previous == null) {
            persistTransaction(transactionToSave)
        } else {
            confirmAction(R.string.confirm_update, R.string.update_question, R.string.update) {
                persistTransaction(transactionToSave)
            }
        }
    }

    private fun persistTransaction(transaction: Transaction) {
        TransactionViewModel.saveTransaction(transaction)
        fillFields(transaction)
        toast(R.string.saved)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("editing_id", existing?.id)
        outState.putString("type", type.name)
        outState.putString("category", category.name)
        outState.putString("date", date.toString())
        super.onSaveInstanceState(outState)
    }
}
