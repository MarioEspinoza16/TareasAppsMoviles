package com.marioespinoza.mibalance.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.marioespinoza.mibalance.R
import com.marioespinoza.mibalance.databinding.ActivityTransactionEditBinding
import com.marioespinoza.mibalance.domain.model.*
import com.marioespinoza.mibalance.util.*
import com.marioespinoza.mibalance.viewmodel.TransactionEditViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate
import java.util.UUID

class TransactionEditActivity : BaseActivity() {
    private lateinit var binding: ActivityTransactionEditBinding
    private lateinit var model: TransactionEditViewModel
    private var id = UUID.randomUUID().toString()
    private var date = LocalDate.now()
    private var type = TransactionType.EXPENSE
    private var categories = emptyList<Category>()
    private var receiptPath: String? = null
    private var originalReceipt: String? = null
    private var cameraPath: String? = null
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var loaded = false
    private var committed = false
    private var importing = false
    private var restoredCategory: Category? = null
    private val gallery = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri -> if (uri != null) importPhoto(uri) }
    private val camera = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        val path = cameraPath; cameraPath = null
        if (success && path != null) {
            lifecycleScope.launch {
                importing = true; setBusy(true)
                val result = withContext(Dispatchers.IO) { runCatching { ReceiptFiles.import(this@TransactionEditActivity, Uri.fromFile(File(path))) } }
                File(path).delete()
                result.onSuccess { replaceReceipt(it) }.onFailure { toast(R.string.error_photo) }
                importing = false; setBusy(false)
            }
        } else path?.let { File(it).delete() }
    }
    private val map = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) result.data?.let {
            latitude = it.getDoubleExtra("latitude", 0.0); longitude = it.getDoubleExtra("longitude", 0.0); showLocation()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionEditBinding.inflate(layoutInflater); setContentView(binding.root); setup(binding.root, binding.toolbar)
        model = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application))[TransactionEditViewModel::class.java]
        val existingId = intent.getStringExtra("transaction_id")
        id = savedInstanceState?.getString("draft_id") ?: existingId ?: id
        type = (savedInstanceState?.getString("type") ?: intent.getStringExtra("type"))?.let(TransactionType::valueOf) ?: type
        if (savedInstanceState != null) {
            date = LocalDate.parse(savedInstanceState.getString("date")); receiptPath = savedInstanceState.getString("receipt")
            originalReceipt = savedInstanceState.getString("original_receipt"); cameraPath = savedInstanceState.getString("camera")
            if (savedInstanceState.containsKey("latitude")) { latitude = savedInstanceState.getDouble("latitude"); longitude = savedInstanceState.getDouble("longitude") }
            loaded = savedInstanceState.getBoolean("loaded"); restoredCategory = savedInstanceState.getString("category")?.let(Category::valueOf)
        }
        if (existingId != null) binding.toolbar.setTitle(R.string.edit_transaction)
        fillSpinner(binding.type, listOf(getString(R.string.income), getString(R.string.expense)), if (type == TransactionType.INCOME) 0 else 1)
        updateCategories(restoredCategory)
        binding.type.onItemSelectedListener = listener { index ->
            val next = if (index == 0) TransactionType.INCOME else TransactionType.EXPENSE
            if (type != next) { type = next; updateCategories() }
        }
        binding.category.onItemSelectedListener = listener { showCategoryFields() }
        binding.date.setOnClickListener { DatePickerDialog(this, { _, y, m, d -> date = LocalDate.of(y, m + 1, d); showDate() }, date.year, date.monthValue - 1, date.dayOfMonth).show() }
        binding.gallery.setOnClickListener { gallery.launch(arrayOf("image/*")) }
        binding.camera.setOnClickListener {
            val file = ReceiptFiles.create(this); cameraPath = file.absolutePath
            try { camera.launch(FileProvider.getUriForFile(this, "$packageName.files", file)) }
            catch (_: Exception) { file.delete(); cameraPath = null; toast(R.string.error_camera) }
        }
        binding.removePhoto.setOnClickListener { replaceReceipt(null) }
        binding.chooseLocation.setOnClickListener {
            val intent = Intent(this, LocationActivity::class.java)
            if (latitude != null) { intent.putExtra("latitude", latitude!!); intent.putExtra("longitude", longitude!!) }
            map.launch(intent)
        }
        binding.removeLocation.setOnClickListener { latitude = null; longitude = null; showLocation() }
        binding.save.setOnClickListener { save() }
        binding.cancel.setOnClickListener { requestExit() }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) { override fun handleOnBackPressed() { requestExit() } })
        model.loaded.observe(this) { result ->
            if (!loaded) result.onSuccess { item ->
                if (item == null) { toast(R.string.error_not_found); finish() }
                else {
                    type = item.type; date = item.date; receiptPath = item.receiptPath; originalReceipt = item.receiptPath
                    latitude = item.latitude; longitude = item.longitude
                    fillSpinner(binding.type, listOf(getString(R.string.income), getString(R.string.expense)), if (type == TransactionType.INCOME) 0 else 1)
                    updateCategories(item.category)
                    binding.amount.setText(Money.input(item.amountMinor)); binding.description.setText(item.description)
                    binding.odometer.setText(item.odometerKm?.let { String.format(java.util.Locale.US, "%d", it) }.orEmpty()); binding.liters.setText(item.fuelLiters.orEmpty())
                    loaded = true; setBusy(false); showDate(); showReceipt(); showLocation()
                }
            }.onFailure { toast(R.string.error_load); finish() }
        }
        model.saved.observe(this) { result ->
            result.onSuccess {
                committed = true
                if (originalReceipt != receiptPath) ReceiptFiles.remove(this, originalReceipt)
                toast(R.string.saved); finish()
            }.onFailure { setBusy(false); toast(R.string.error_save) }
        }
        if (existingId != null && !loaded) { setBusy(true); model.load(existingId) }
        showDate(); showReceipt(); showLocation(); showCategoryFields()
        if (model.saving) setBusy(true)
    }
    private fun listener(action: (Int) -> Unit) = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) = action(position)
        override fun onNothingSelected(parent: AdapterView<*>?) = Unit
    }
    private fun updateCategories(selected: Category? = null) {
        categories = Category.entries.filter { it.type == type }
        fillSpinner(binding.category, categories.map { getString(it.labelRes) }, categories.indexOf(selected).coerceAtLeast(0))
        showCategoryFields()
    }
    private fun showCategoryFields() {
        val category = categories.getOrNull(binding.category.selectedItemPosition) ?: return
        binding.descriptionLayout.hint = getString(when(category) { Category.MAINTENANCE -> R.string.maintenance_detail; Category.REPAIRS -> R.string.repair_detail; else -> R.string.description })
        binding.odometerLayout.visibility = if (category.isVehicle) View.VISIBLE else View.GONE
        binding.litersLayout.visibility = if (category == Category.FUEL) View.VISIBLE else View.GONE
        binding.chooseLocation.visibility = if (type == TransactionType.EXPENSE) View.VISIBLE else View.GONE
        binding.location.visibility = if (type == TransactionType.EXPENSE) View.VISIBLE else View.GONE
        binding.removeLocation.visibility = if (type == TransactionType.EXPENSE && latitude != null) View.VISIBLE else View.GONE
    }
    private fun showDate() { binding.date.text = getString(R.string.date_button, dateText(date)) }
    private fun showLocation() {
        binding.location.text = if (latitude == null) getString(R.string.no_location) else getString(R.string.location_value, "%.5f".format(java.util.Locale.US, latitude), "%.5f".format(java.util.Locale.US, longitude))
        binding.removeLocation.visibility = if (latitude == null || type == TransactionType.INCOME) View.GONE else View.VISIBLE
    }
    private fun showReceipt() {
        val bitmap = receiptPath?.let(ReceiptFiles::preview)
        binding.receipt.setImageBitmap(bitmap)
        binding.receipt.visibility = if (bitmap == null) View.GONE else View.VISIBLE
        binding.removePhoto.visibility = if (receiptPath == null) View.GONE else View.VISIBLE
    }
    private fun replaceReceipt(path: String?) {
        if (receiptPath != originalReceipt) ReceiptFiles.remove(this, receiptPath)
        receiptPath = path; showReceipt()
    }
    private fun importPhoto(uri: Uri) {
        importing = true; setBusy(true)
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) { runCatching { ReceiptFiles.import(this@TransactionEditActivity, uri) } }
            result.onSuccess { replaceReceipt(it) }.onFailure { toast(R.string.error_photo) }
            importing = false; setBusy(false)
        }
    }
    private fun setBusy(busy: Boolean) {
        binding.save.isEnabled = !busy; binding.cancel.isEnabled = !busy; binding.gallery.isEnabled = !busy; binding.camera.isEnabled = !busy
        binding.type.isEnabled = !busy; binding.category.isEnabled = !busy; binding.amount.isEnabled = !busy; binding.description.isEnabled = !busy
        binding.date.isEnabled = !busy; binding.odometer.isEnabled = !busy; binding.liters.isEnabled = !busy
        binding.removePhoto.isEnabled = !busy; binding.chooseLocation.isEnabled = !busy; binding.removeLocation.isEnabled = !busy
    }
    private fun save() {
        binding.amountLayout.error = null; binding.descriptionLayout.error = null; binding.odometerLayout.error = null; binding.litersLayout.error = null
        val amount = Money.parseMinor(binding.amount.text.toString())
        if (amount == null) { binding.amountLayout.error = getString(R.string.error_amount); binding.amount.requestFocus(); return }
        val category = categories[binding.category.selectedItemPosition]
        val description = binding.description.text.toString().trim()
        if (category in setOf(Category.MAINTENANCE, Category.REPAIRS) && description.isBlank()) {
            binding.descriptionLayout.error = getString(R.string.error_description); binding.description.requestFocus(); return
        }
        val rawOdometer = binding.odometer.text.toString().trim()
        val odometer = if (category.isVehicle && rawOdometer.isNotEmpty()) rawOdometer.toLongOrNull() else null
        if (category.isVehicle && rawOdometer.isNotEmpty() && (odometer == null || odometer < 0)) { binding.odometerLayout.error = getString(R.string.error_number); return }
        val rawLiters = binding.liters.text.toString().trim().replace(',', '.')
        val liters = if (category == Category.FUEL && rawLiters.isNotEmpty()) rawLiters.toBigDecimalOrNull()?.takeIf { it.signum() > 0 }?.toPlainString() else null
        if (category == Category.FUEL && rawLiters.isNotEmpty() && liters == null) { binding.litersLayout.error = getString(R.string.error_number); return }
        val item = Transaction(id, type, amount, date, category, description, odometer, liters, receiptPath,
            if (type == TransactionType.EXPENSE) latitude else null, if (type == TransactionType.EXPENSE) longitude else null)
        setBusy(true); model.save(item)
    }
    private fun requestExit() {
        if (model.saving || importing) return
        MaterialAlertDialogBuilder(this).setTitle(R.string.discard_title).setMessage(R.string.discard_message)
            .setNegativeButton(R.string.cancel, null).setPositiveButton(R.string.discard) { _, _ -> finish() }.show()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("draft_id", id); outState.putString("date", date.toString()); outState.putString("type", type.name)
        outState.putString("category", categories.getOrNull(binding.category.selectedItemPosition)?.name)
        outState.putString("receipt", receiptPath); outState.putString("original_receipt", originalReceipt); outState.putString("camera", cameraPath)
        outState.putBoolean("loaded", loaded)
        latitude?.let { outState.putDouble("latitude", it); outState.putDouble("longitude", longitude!!) }
        super.onSaveInstanceState(outState)
    }
    override fun onDestroy() {
        if (isFinishing && !committed && !model.saving) {
            if (receiptPath != originalReceipt) ReceiptFiles.remove(this, receiptPath)
            ReceiptFiles.remove(this, cameraPath)
        }
        super.onDestroy()
    }
}
