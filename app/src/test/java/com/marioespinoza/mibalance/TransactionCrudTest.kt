package com.marioespinoza.mibalance

import android.content.Intent
import android.content.ClipboardManager
import android.content.Context
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.marioespinoza.mibalance.domain.model.Category
import com.marioespinoza.mibalance.domain.model.Transaction
import com.marioespinoza.mibalance.domain.model.TransactionType
import com.marioespinoza.mibalance.ui.DashboardActivity
import com.marioespinoza.mibalance.ui.DetailActivity
import com.marioespinoza.mibalance.ui.HistoryActivity
import com.marioespinoza.mibalance.ui.TransactionEditActivity
import com.marioespinoza.mibalance.ui.OnTransactionClickListener
import com.marioespinoza.mibalance.ui.TransactionListAdapter
import com.marioespinoza.mibalance.viewmodel.TransactionViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowDialog
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], qualifiers = "en")
class TransactionCrudTest {
    @Before fun clearTransactions() {
        TransactionViewModel.getAllTransactions().forEach { TransactionViewModel.removeTransaction(it.id) }
    }

    private fun respondToDialog(button: Int) {
        (ShadowDialog.getLatestDialog() as AlertDialog).getButton(button).performClick()
        shadowOf(Looper.getMainLooper()).idle()
    }

    private fun editor(id: String? = null): TransactionEditActivity {
        val intent = Intent(RuntimeEnvironment.getApplication(), TransactionEditActivity::class.java)
        id?.let { intent.putExtra("transaction_id", it) }
        return Robolectric.buildActivity(TransactionEditActivity::class.java, intent).setup().get()
    }

    private fun seed(date: LocalDate = LocalDate.now()): Transaction =
        TransactionViewModel.saveTransaction(Transaction(type = TransactionType.EXPENSE, amountMinor = 1250050,
            date = date, category = Category.FUEL, description = "Morning fuel"))

    @Test fun savingOpensMovementsAndUpdatingRequiresConfirmationWithoutDuplicating() {
        val create = editor()
        create.findViewById<EditText>(R.id.amount).setText("12500.50")
        create.findViewById<EditText>(R.id.description).setText("Morning fuel")
        create.findViewById<Button>(R.id.save).performClick()
        val original = TransactionViewModel.getAllTransactions().single()
        assertTrue(original.id.toLong() > 0)
        assertTrue(create.isFinishing)
        assertEquals(HistoryActivity::class.java.name, shadowOf(create).nextStartedActivity.component?.className)

        val activity = editor(original.id)
        activity.findViewById<EditText>(R.id.amount).setText("15000")
        activity.findViewById<Button>(R.id.save).performClick()
        assertEquals(original, TransactionViewModel.getTransaction(original.id))
        respondToDialog(AlertDialog.BUTTON_NEGATIVE)
        assertEquals(original, TransactionViewModel.getTransaction(original.id))
        activity.findViewById<Button>(R.id.save).performClick()
        respondToDialog(AlertDialog.BUTTON_POSITIVE)
        val updated = TransactionViewModel.getAllTransactions().single()
        assertEquals(original.id, updated.id)
        assertEquals(1500000L, updated.amountMinor)
        assertTrue(activity.isFinishing)
    }

    @Test fun movementsShowsAllDatesSearchesExactIdAndOpensTheSelectedDetail() {
        val original = seed(LocalDate.of(2025, 1, 1))
        repeat(49) { seed() }
        val activity = Robolectric.buildActivity(HistoryActivity::class.java).setup().get()
        val recycler = activity.findViewById<RecyclerView>(R.id.transactions)
        assertEquals(50, recycler.adapter?.itemCount)
        val query = activity.findViewById<EditText>(R.id.searchQuery)
        query.setText(original.id)
        activity.findViewById<Button>(R.id.search).performClick()
        assertEquals(1, recycler.adapter?.itemCount)
        val adapter = recycler.adapter as TransactionListAdapter
        val holder = adapter.onCreateViewHolder(recycler, 0)
        adapter.onBindViewHolder(holder, 0)
        assertEquals(activity.getString(R.string.transaction_id_value, original.id), holder.transactionId.text.toString())
        assertEquals(original.description, holder.description.text.toString())
        holder.itemView.performClick()
        val intent = shadowOf(activity).nextStartedActivity
        assertEquals(DetailActivity::class.java.name, intent.component?.className)
        assertEquals(original.id, intent.getStringExtra("transaction_id"))
        query.setText("999999")
        activity.findViewById<Button>(R.id.search).performClick()
        assertEquals(0, recycler.adapter?.itemCount)
        activity.findViewById<Button>(R.id.showAll).performClick()
        assertEquals(50, recycler.adapter?.itemCount)
        assertEquals("", query.text.toString())
        activity.findViewById<Button>(R.id.search).performClick()
        assertNotNull(query.error)
    }

    @Test fun deletingFromEditorCanBeCancelledThenConfirmedAndReturnsToMovements() {
        val original = seed()
        val activity = editor(original.id)
        activity.findViewById<Button>(R.id.delete).performClick()
        respondToDialog(AlertDialog.BUTTON_NEGATIVE)
        assertEquals(original, TransactionViewModel.getTransaction(original.id))
        activity.findViewById<Button>(R.id.delete).performClick()
        respondToDialog(AlertDialog.BUTTON_POSITIVE)
        assertNull(TransactionViewModel.getTransaction(original.id))
        assertTrue(activity.isFinishing)
        assertEquals(HistoryActivity::class.java.name, shadowOf(activity).nextStartedActivity.component?.className)
    }

    @Test fun detailShowsNumericIdAndDeletionRequiresTheDialog() {
        val original = seed()
        val intent = Intent(RuntimeEnvironment.getApplication(), DetailActivity::class.java)
            .putExtra("transaction_id", original.id)
        val activity = Robolectric.buildActivity(DetailActivity::class.java, intent).setup().get()
        assertEquals(activity.getString(R.string.transaction_id_value, original.id),
            activity.findViewById<TextView>(R.id.transactionId).text.toString())
        activity.findViewById<Button>(R.id.delete).performClick()
        respondToDialog(AlertDialog.BUTTON_NEGATIVE)
        assertEquals(original, TransactionViewModel.getTransaction(original.id))
        activity.findViewById<Button>(R.id.delete).performClick()
        respondToDialog(AlertDialog.BUTTON_POSITIVE)
        assertNull(TransactionViewModel.getTransaction(original.id))
        assertTrue(activity.isFinishing)
    }

    @Test fun changingToNewModeSurvivesRecreationWithoutOverwritingTheOriginal() {
        val original = seed()
        val intent = Intent(RuntimeEnvironment.getApplication(), TransactionEditActivity::class.java)
            .putExtra("transaction_id", original.id)
        val controller = Robolectric.buildActivity(TransactionEditActivity::class.java, intent).setup()
        controller.get().findViewById<Button>(R.id.newTransaction).performClick()
        controller.get().findViewById<EditText>(R.id.amount).setText("200")
        controller.get().findViewById<EditText>(R.id.description).setText("Second expense")
        controller.recreate()
        controller.get().findViewById<Button>(R.id.save).performClick()
        assertEquals(original, TransactionViewModel.getTransaction(original.id))
        assertEquals(2, TransactionViewModel.getAllTransactions().size)
    }

    @Test fun listRowsShowAndCopyTheirOwnIdAfterRebindingWithoutOpeningTheDetail() {
        val original = seed()
        val second = original.copy(id = (original.id.toLong() + 1).toString(), description = "Second fuel purchase")
        val activity = editor()
        var openedId: String? = null
        val adapter = TransactionListAdapter(listOf(original, second), object : OnTransactionClickListener {
            override fun onTransactionClick(transaction: Transaction) { openedId = transaction.id }
        })
        val recycler = RecyclerView(activity)
        recycler.layoutManager = LinearLayoutManager(activity)
        val holder = adapter.onCreateViewHolder(recycler, 0)
        val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        adapter.onBindViewHolder(holder, 0)
        assertEquals(activity.getString(R.string.transaction_id_value, original.id), holder.transactionId.text.toString())
        holder.copyId.performClick()
        assertEquals(original.id, clipboard.primaryClip?.getItemAt(0)?.text.toString())
        assertNull(openedId)
        adapter.onBindViewHolder(holder, 1)
        holder.copyId.performClick()
        assertEquals(second.id, clipboard.primaryClip?.getItemAt(0)?.text.toString())
        assertNull(openedId)
        holder.itemView.performClick()
        assertEquals(second.id, openedId)
    }

    @Test fun dashboardAndFormHaveADirectMovementsButtonAndNameIsRequired() {
        val dashboard = Robolectric.buildActivity(DashboardActivity::class.java).setup().get()
        dashboard.findViewById<Button>(R.id.history).performClick()
        assertEquals(HistoryActivity::class.java.name, shadowOf(dashboard).nextStartedActivity.component?.className)
        val activity = editor()
        activity.findViewById<EditText>(R.id.amount).setText("100")
        activity.findViewById<Button>(R.id.save).performClick()
        assertNotNull(activity.findViewById<EditText>(R.id.description).error)
        assertTrue(TransactionViewModel.getAllTransactions().isEmpty())
        activity.findViewById<Button>(R.id.movements).performClick()
        assertEquals(HistoryActivity::class.java.name, shadowOf(activity).nextStartedActivity.component?.className)
    }

    @Test fun returningToMovementsClearsThePreviousSearchAndShowsTheSavedRecords() {
        val first = seed()
        val controller = Robolectric.buildActivity(HistoryActivity::class.java).setup()
        val activity = controller.get()
        activity.findViewById<EditText>(R.id.searchQuery).setText(first.id)
        activity.findViewById<Button>(R.id.search).performClick()
        seed()
        controller.newIntent(Intent(activity, HistoryActivity::class.java))
        assertEquals("", activity.findViewById<EditText>(R.id.searchQuery).text.toString())
        assertEquals(2, activity.findViewById<RecyclerView>(R.id.transactions).adapter?.itemCount)
        activity.findViewById<EditText>(R.id.searchQuery).setText(first.id)
        activity.findViewById<Button>(R.id.search).performClick()
        TransactionViewModel.removeTransaction(first.id)
        controller.pause().resume()
        assertEquals("", activity.findViewById<EditText>(R.id.searchQuery).text.toString())
        assertEquals(1, activity.findViewById<RecyclerView>(R.id.transactions).adapter?.itemCount)
    }
}
