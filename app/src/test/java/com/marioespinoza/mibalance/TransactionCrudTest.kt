package com.marioespinoza.mibalance

import android.content.Intent
import android.content.ClipboardManager
import android.content.Context
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.marioespinoza.mibalance.domain.model.Category
import com.marioespinoza.mibalance.domain.model.Transaction
import com.marioespinoza.mibalance.domain.model.TransactionFilter
import com.marioespinoza.mibalance.domain.model.TransactionType
import com.marioespinoza.mibalance.ui.DetailActivity
import com.marioespinoza.mibalance.ui.TransactionEditActivity
import com.marioespinoza.mibalance.ui.OnTransactionClickListener
import com.marioespinoza.mibalance.ui.TransactionListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
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
import java.time.YearMonth

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], qualifiers = "en")
class TransactionCrudTest {
    private val filter = TransactionFilter(YearMonth.now())

    @Before fun clearTransactions() {
        TransactionViewModel.getTransactions(filter).forEach { TransactionViewModel.removeTransaction(it.id) }
    }

    private fun currentDialog() = ShadowDialog.getLatestDialog() as AlertDialog

    private fun respondToDialog(button: Int) {
        currentDialog().getButton(button).performClick()
        shadowOf(Looper.getMainLooper()).idle()
    }

    private fun editor(id: String? = null): TransactionEditActivity {
        val intent = Intent(RuntimeEnvironment.getApplication(), TransactionEditActivity::class.java)
        id?.let { intent.putExtra("transaction_id", it) }
        return Robolectric.buildActivity(TransactionEditActivity::class.java, intent).setup().get()
    }

    private fun seed(): Transaction {
        val transaction = Transaction(type = TransactionType.EXPENSE, amountMinor = 1250050,
            date = LocalDate.now(), category = Category.FUEL, description = "Morning fuel")
        TransactionViewModel.saveTransaction(transaction)
        return transaction
    }

    @Test fun createThenUpdateRequiresConfirmationAndKeepsTheSameId() {
        val activity = editor()
        activity.findViewById<EditText>(R.id.amount).setText("12500.50")
        activity.findViewById<EditText>(R.id.description).setText("Morning fuel")
        activity.findViewById<Button>(R.id.save).performClick()
        val original = TransactionViewModel.getTransactions(filter).single()
        assertTrue(activity.findViewById<TextView>(R.id.transactionId).text.contains(original.id))
        assertEquals(android.view.View.VISIBLE, activity.findViewById<Button>(R.id.delete).visibility)

        activity.findViewById<EditText>(R.id.amount).setText("15000")
        activity.findViewById<Button>(R.id.save).performClick()
        assertEquals(original, TransactionViewModel.getTransaction(original.id))
        respondToDialog(AlertDialog.BUTTON_NEGATIVE)
        assertEquals(original, TransactionViewModel.getTransaction(original.id))

        activity.findViewById<Button>(R.id.save).performClick()
        respondToDialog(AlertDialog.BUTTON_POSITIVE)
        val updated = TransactionViewModel.getTransactions(filter).single()
        assertEquals(original.id, updated.id)
        assertEquals(1500000L, updated.amountMinor)
    }

    @Test fun searchLoadsTheSelectedTransactionAndUnknownQueryDoesNotChangeIt() {
        val original = seed()
        val activity = editor()
        val query = activity.findViewById<EditText>(R.id.searchQuery)
        query.setText("  MORNING  ")
        activity.findViewById<Button>(R.id.search).performClick()
        val dialog = currentDialog()
        dialog.listView.performItemClick(dialog.listView.getChildAt(0), 0, 0)
        assertEquals("12500.50", activity.findViewById<EditText>(R.id.amount).text.toString())
        assertTrue(activity.findViewById<TextView>(R.id.transactionId).text.contains(original.id))
        query.setText("does not exist")
        activity.findViewById<Button>(R.id.search).performClick()
        assertTrue(activity.findViewById<TextView>(R.id.transactionId).text.contains(original.id))
        query.setText("")
        activity.findViewById<Button>(R.id.search).performClick()
        assertNotNull(query.error)
    }

    @Test fun deletingFromEditorCanBeCancelledAndResetsToCreateModeWhenConfirmed() {
        val original = seed()
        val activity = editor(original.id)
        activity.findViewById<Button>(R.id.delete).performClick()
        respondToDialog(AlertDialog.BUTTON_NEGATIVE)
        assertEquals(original, TransactionViewModel.getTransaction(original.id))
        activity.findViewById<Button>(R.id.delete).performClick()
        respondToDialog(AlertDialog.BUTTON_POSITIVE)
        assertNull(TransactionViewModel.getTransaction(original.id))
        assertEquals(android.view.View.GONE, activity.findViewById<Button>(R.id.delete).visibility)
        assertEquals("", activity.findViewById<EditText>(R.id.amount).text.toString())
        activity.findViewById<EditText>(R.id.amount).setText("100")
        activity.findViewById<Button>(R.id.save).performClick()
        assertNotEquals(original.id, TransactionViewModel.getTransactions(filter).single().id)
    }

    @Test fun detailDeletionAlsoRequiresTheDialog() {
        val original = seed()
        val intent = Intent(RuntimeEnvironment.getApplication(), DetailActivity::class.java)
            .putExtra("transaction_id", original.id)
        val activity = Robolectric.buildActivity(DetailActivity::class.java, intent).setup().get()
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
        controller.recreate()
        controller.get().findViewById<Button>(R.id.save).performClick()
        assertEquals(original, TransactionViewModel.getTransaction(original.id))
        assertEquals(2, TransactionViewModel.getTransactions(filter).size)
    }

    @Test fun listRowsShowAndCopyTheirOwnIdAfterRebindingWithoutOpeningTheDetail() {
        val original = seed()
        val second = original.copy(id = "another-transaction-id", description = "Second fuel purchase")
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
        assertEquals(activity.getString(R.string.transaction_id_value, original.id), holder.transactionId.text)
        holder.copyId.performClick()
        assertEquals(original.id, clipboard.primaryClip?.getItemAt(0)?.text.toString())
        assertNull(openedId)
        adapter.onBindViewHolder(holder, 1)
        assertEquals(activity.getString(R.string.transaction_id_value, second.id), holder.transactionId.text)
        holder.copyId.performClick()
        assertEquals(second.id, clipboard.primaryClip?.getItemAt(0)?.text.toString())
        assertNull(openedId)
        holder.itemView.performClick()
        assertEquals(second.id, openedId)
    }
}
