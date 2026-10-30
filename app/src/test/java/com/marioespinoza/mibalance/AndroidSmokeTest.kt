package com.marioespinoza.mibalance

import android.content.Intent
import android.widget.Spinner
import com.marioespinoza.mibalance.data.local.LocalTransactionRepository
import com.marioespinoza.mibalance.domain.model.*
import com.marioespinoza.mibalance.ui.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26, 35])
class AndroidSmokeTest {
    @Test fun localRepositorySurvivesReopeningAndSupportsUpdateDelete() {
        val context = RuntimeEnvironment.getApplication()
        context.deleteDatabase("mibalance.db")
        val row = Transaction(type = TransactionType.EXPENSE, amountMinor = 123456, date = LocalDate.of(2026, 10, 30), category = Category.MAINTENANCE, description = "Oil and filter", odometerKm = 80000, receiptPath = "/test/receipt.jpg", latitude = 9.97, longitude = -84.83)
        val first = LocalTransactionRepository(context)
        first.save(row); first.close()
        val reopened = LocalTransactionRepository(context)
        try {
            val it = reopened
            assertEquals(row, it.findById(row.id))
            it.save(row.copy(amountMinor = 22222))
            assertEquals(1, it.getAll().size); assertEquals(22222L, it.findById(row.id)?.amountMinor)
            it.delete(row.id); assertTrue(it.getAll().isEmpty())
        } finally { reopened.close() }
    }
    @Test fun allFiveActivityLayoutsInflate() {
        val context = RuntimeEnvironment.getApplication() as BalanceApplication
        val row = Transaction(type = TransactionType.EXPENSE, amountMinor = 500000, date = LocalDate.now(), category = Category.FUEL)
        context.repository.save(row)
        Robolectric.buildActivity(DashboardActivity::class.java).setup().close()
        Robolectric.buildActivity(HistoryActivity::class.java).setup().close()
        Robolectric.buildActivity(TransactionEditActivity::class.java).setup().close()
        Robolectric.buildActivity(DetailActivity::class.java, Intent(context, DetailActivity::class.java).putExtra("transaction_id", row.id)).setup().close()
        Robolectric.buildActivity(LocationActivity::class.java).setup().close()
    }
    @Test fun editorHasIncomeAndExpenseAndCategoryChoices() {
        val controller = Robolectric.buildActivity(TransactionEditActivity::class.java).setup()
        val activity = controller.get()
        assertEquals(2, activity.findViewById<Spinner>(R.id.type).adapter.count)
        assertEquals(7, activity.findViewById<Spinner>(R.id.category).adapter.count)
        controller.close()
    }
    @Test fun incomeCanBeSavedThroughTheForm() {
        val context = RuntimeEnvironment.getApplication() as BalanceApplication
        val controller = Robolectric.buildActivity(TransactionEditActivity::class.java, Intent(context, TransactionEditActivity::class.java).putExtra("type", "INCOME")).setup()
        val activity = controller.get()
        assertSame("Activity application", context, activity.application)
        val model = androidx.lifecycle.ViewModelProvider(activity)[com.marioespinoza.mibalance.viewmodel.TransactionEditViewModel::class.java]
        assertSame("ViewModel application", context, model.getApplication<android.app.Application>())
        activity.findViewById<android.widget.EditText>(R.id.amount).setText("30000,50")
        activity.findViewById<android.view.View>(R.id.save).performClick()
        var attempts = 0
        while (!activity.isFinishing && attempts++ < 100) {
            Thread.sleep(20)
            org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
        }
        assertTrue(activity.isFinishing)
        val rows = context.repository.getAll()
        assertEquals(1, rows.size); assertEquals(3000050L, rows[0].amountMinor)
        assertEquals(TransactionType.INCOME, rows[0].type)
        controller.close()
    }
    @Test fun maintenanceWithoutDescriptionCannotBeSaved() {
        val controller = Robolectric.buildActivity(TransactionEditActivity::class.java).setup()
        val activity = controller.get()
        activity.findViewById<android.widget.EditText>(R.id.amount).setText("5000")
        activity.findViewById<Spinner>(R.id.category).setSelection(2)
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
        activity.findViewById<android.view.View>(R.id.save).performClick()
        assertNotNull(activity.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.descriptionLayout).error)
        assertFalse(activity.isFinishing)
        controller.close()
    }
    @Test fun spanishResourcesExist() {
        val context = RuntimeEnvironment.getApplication()
        val config = android.content.res.Configuration(context.resources.configuration)
        config.setLocale(java.util.Locale.forLanguageTag("es"))
        assertEquals("Guardar movimiento", context.createConfigurationContext(config).getString(R.string.save))
    }
}
