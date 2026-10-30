package com.marioespinoza.mibalance.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.marioespinoza.mibalance.R
import com.marioespinoza.mibalance.databinding.ActivityDetailBinding
import com.marioespinoza.mibalance.domain.model.*
import com.marioespinoza.mibalance.util.ReceiptFiles
import com.marioespinoza.mibalance.util.labelRes
import com.marioespinoza.mibalance.viewmodel.DetailViewModel

class DetailActivity : BaseActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var model: DetailViewModel
    private lateinit var id: String
    private var item: Transaction? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id = intent.getStringExtra("transaction_id") ?: run { finish(); return }
        binding = ActivityDetailBinding.inflate(layoutInflater); setContentView(binding.root); setup(binding.root, binding.toolbar)
        model = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application))[DetailViewModel::class.java]
        binding.edit.isEnabled = false; binding.delete.isEnabled = false
        binding.edit.setOnClickListener { startActivity(Intent(this, TransactionEditActivity::class.java).putExtra("transaction_id", id)) }
        binding.delete.setOnClickListener {
            MaterialAlertDialogBuilder(this).setTitle(R.string.delete_title).setMessage(R.string.delete_message)
                .setNegativeButton(R.string.cancel, null).setPositiveButton(R.string.delete) { _, _ ->
                    binding.delete.isEnabled = false; binding.edit.isEnabled = false; model.delete(id)
                }.show()
        }
        binding.viewLocation.setOnClickListener { item?.let { row ->
            if (row.latitude != null) startActivity(Intent(this, LocationActivity::class.java).putExtra("latitude", row.latitude).putExtra("longitude", row.longitude!!).putExtra("read_only", true))
        } }
        model.state.observe(this) { result -> result.onSuccess { row ->
            if (row == null) { toast(R.string.error_not_found); finish() }
            else { item = row; render(row); binding.edit.isEnabled = !model.deleting; binding.delete.isEnabled = !model.deleting }
        }.onFailure { toast(R.string.error_load) } }
        model.deleted.observe(this) { result -> result.onSuccess {
            ReceiptFiles.remove(this, item?.receiptPath); toast(R.string.deleted); finish()
        }.onFailure { binding.delete.isEnabled = true; binding.edit.isEnabled = true; toast(R.string.error_delete) } }
    }
    override fun onResume() { super.onResume(); model.load(id) }
    private fun render(row: Transaction) {
        binding.category.setText(row.category.labelRes)
        binding.amount.text = money(row.amountMinor)
        binding.amount.setTextColor(getColor(if (row.type == TransactionType.INCOME) R.color.income else R.color.expense))
        binding.type.setText(if (row.type == TransactionType.INCOME) R.string.income else R.string.expense)
        binding.date.text = dateText(row.date)
        binding.description.text = row.description.ifBlank { getString(R.string.no_description) }
        binding.odometer.visibility = if (row.odometerKm == null) View.GONE else View.VISIBLE
        binding.odometer.text = getString(R.string.odometer_value, row.odometerKm?.toString().orEmpty())
        binding.liters.visibility = if (row.fuelLiters == null) View.GONE else View.VISIBLE
        binding.liters.text = getString(R.string.liters_value, row.fuelLiters.orEmpty())
        val photo = row.receiptPath?.let(ReceiptFiles::preview)
        binding.receipt.setImageBitmap(photo); binding.receipt.visibility = if (photo == null) View.GONE else View.VISIBLE
        if (row.receiptPath != null && photo == null) toast(R.string.receipt_missing)
        binding.location.text = if (row.latitude == null) getString(R.string.no_location) else getString(R.string.location_value, "%.5f".format(java.util.Locale.US, row.latitude), "%.5f".format(java.util.Locale.US, row.longitude))
        binding.viewLocation.visibility = if (row.latitude == null) View.GONE else View.VISIBLE
    }
}
