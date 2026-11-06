package com.marioespinoza.mibalance.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.marioespinoza.mibalance.R
import com.marioespinoza.mibalance.domain.model.TransactionType
import com.marioespinoza.mibalance.util.dateText
import com.marioespinoza.mibalance.util.labelRes
import com.marioespinoza.mibalance.util.money
import com.marioespinoza.mibalance.util.setupScreen
import com.marioespinoza.mibalance.util.toast
import com.marioespinoza.mibalance.viewmodel.TransactionViewModel

class DetailActivity : AppCompatActivity() {
    private var confirmDelete = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setupScreen()
    }

    override fun onResume() {
        super.onResume()
        val id = intent.getStringExtra("transaction_id")
        val transaction = if (id == null) null else TransactionViewModel.getTransaction(id)
        if (transaction == null) {
            toast(R.string.error_not_found)
            finish()
            return
        }

        val income = transaction.type == TransactionType.INCOME
        findViewById<TextView>(R.id.category).setText(transaction.category.labelRes)
        findViewById<TextView>(R.id.amount).text = money(transaction.amountMinor)
        findViewById<TextView>(R.id.amount).setTextColor(getColor(if (income) R.color.income else R.color.expense))
        findViewById<TextView>(R.id.type).setText(if (income) R.string.income else R.string.expense)
        findViewById<TextView>(R.id.date).text = dateText(transaction.date)
        findViewById<TextView>(R.id.description).text = transaction.description.ifBlank { getString(R.string.no_description) }

        val odometer = findViewById<TextView>(R.id.odometer)
        odometer.visibility = if (transaction.odometerKm == null) View.GONE else View.VISIBLE
        odometer.text = getString(R.string.odometer_value, transaction.odometerKm?.toString().orEmpty())
        val liters = findViewById<TextView>(R.id.liters)
        liters.visibility = if (transaction.fuelLiters == null) View.GONE else View.VISIBLE
        liters.text = getString(R.string.liters_value, transaction.fuelLiters.orEmpty())

        findViewById<Button>(R.id.edit).setOnClickListener {
            startActivity(Intent(this, TransactionEditActivity::class.java).putExtra("transaction_id", transaction.id))
        }
        val delete = findViewById<Button>(R.id.delete)
        confirmDelete = false
        delete.setText(R.string.delete)
        delete.setOnClickListener {
            if (confirmDelete) {
                TransactionViewModel.removeTransaction(transaction.id)
                toast(R.string.deleted)
                finish()
            } else {
                confirmDelete = true
                delete.setText(R.string.confirm_delete)
            }
        }
    }
}
