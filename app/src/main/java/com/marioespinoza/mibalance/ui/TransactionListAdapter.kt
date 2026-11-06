package com.marioespinoza.mibalance.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.marioespinoza.mibalance.R
import com.marioespinoza.mibalance.domain.model.Transaction
import com.marioespinoza.mibalance.domain.model.TransactionType
import com.marioespinoza.mibalance.util.Money
import com.marioespinoza.mibalance.util.labelRes
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

interface OnTransactionClickListener {
    fun onTransactionClick(transaction: Transaction)
}

class TransactionListAdapter(
    private val items: List<Transaction>,
    private val listener: OnTransactionClickListener
) : RecyclerView.Adapter<TransactionListAdapter.TransactionHolder>() {
    class TransactionHolder(view: View) : RecyclerView.ViewHolder(view) {
        val category: TextView = view.findViewById(R.id.category)
        val amount: TextView = view.findViewById(R.id.amount)
        val meta: TextView = view.findViewById(R.id.meta)
        val description: TextView = view.findViewById(R.id.description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return TransactionHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: TransactionHolder, position: Int) {
        val transaction = items[position]
        val context = holder.itemView.context
        val locale = context.resources.configuration.locales[0]
        val income = transaction.type == TransactionType.INCOME
        val type = context.getString(if (income) R.string.income else R.string.expense)
        val date = transaction.date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale))

        holder.category.setText(transaction.category.labelRes)
        holder.amount.text = context.getString(
            if (income) R.string.signed_income_amount else R.string.signed_expense_amount,
            Money.format(transaction.amountMinor, locale)
        )
        holder.amount.setTextColor(ContextCompat.getColor(context, if (income) R.color.income else R.color.expense))
        holder.meta.text = context.getString(R.string.transaction_meta, type, date)
        holder.description.text = transaction.description
        holder.description.visibility = if (transaction.description.isBlank()) View.GONE else View.VISIBLE
        holder.itemView.setOnClickListener { listener.onTransactionClick(transaction) }
    }
}
