package com.marioespinoza.mibalance.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.marioespinoza.mibalance.R
import com.marioespinoza.mibalance.domain.model.Transaction
import com.marioespinoza.mibalance.util.openDetail
import com.marioespinoza.mibalance.util.setupScreen
import com.marioespinoza.mibalance.viewmodel.TransactionViewModel

class HistoryActivity : AppCompatActivity(), OnTransactionClickListener {
    private var selectedId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        setupScreen()
        selectedId = savedInstanceState?.getString("selected_id")
        findViewById<EditText>(R.id.searchQuery).setText(selectedId.orEmpty())
        findViewById<RecyclerView>(R.id.transactions).layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.search).setOnClickListener {
            val queryField = findViewById<EditText>(R.id.searchQuery)
            val number = queryField.text.toString().trim().toLongOrNull()
            if (number == null || number <= 0) {
                queryField.error = getString(R.string.error_search_id)
            } else {
                queryField.error = null
                selectedId = number.toString()
                load()
            }
        }
        findViewById<Button>(R.id.showAll).setOnClickListener { showAll() }
        findViewById<Button>(R.id.add).setOnClickListener {
            startActivity(Intent(this, TransactionEditActivity::class.java))
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        showAll()
    }

    override fun onResume() {
        super.onResume()
        val id = selectedId
        if (id != null && TransactionViewModel.getTransaction(id) == null) {
            showAll()
        } else {
            load()
        }
    }

    private fun showAll() {
        selectedId = null
        findViewById<EditText>(R.id.searchQuery).apply {
            setText("")
            error = null
        }
        load()
    }

    private fun load() {
        val id = selectedId
        val rows = if (id == null) {
            TransactionViewModel.getAllTransactions().sortedBy { it.id.toLong() }
        } else {
            listOfNotNull(TransactionViewModel.getTransaction(id))
        }
        findViewById<TextView>(R.id.resultCount).text = getString(R.string.movement_count, rows.size)
        findViewById<TextView>(R.id.empty).apply {
            visibility = if (rows.isEmpty()) View.VISIBLE else View.GONE
            setText(if (id == null) R.string.empty_transactions else R.string.error_not_found)
        }
        findViewById<RecyclerView>(R.id.transactions).adapter = TransactionListAdapter(rows, this)
    }

    override fun onTransactionClick(transaction: Transaction) = openDetail(transaction)

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("selected_id", selectedId)
        super.onSaveInstanceState(outState)
    }
}
