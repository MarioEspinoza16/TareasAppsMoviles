package com.marioespinoza.mibalance.data.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.marioespinoza.mibalance.domain.model.*
import com.marioespinoza.mibalance.domain.repository.TransactionRepository
import java.time.LocalDate

/** Device-only persistence. SQL is confined to the data layer. */
class LocalTransactionRepository(context: Context) : SQLiteOpenHelper(context, "mibalance.db", null, 1), TransactionRepository {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""CREATE TABLE transactions (
            id TEXT PRIMARY KEY, type TEXT NOT NULL, amount INTEGER NOT NULL CHECK(amount > 0),
            date TEXT NOT NULL, category TEXT NOT NULL, description TEXT NOT NULL,
            odometer INTEGER, liters TEXT, receipt TEXT, latitude REAL, longitude REAL
        )""")
        db.execSQL("CREATE INDEX transaction_date ON transactions(date)")
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
    override fun getAll(): List<Transaction> = readableDatabase.query("transactions", null, null, null, null, null, "date DESC, rowid DESC").use { c ->
        buildList { while (c.moveToNext()) add(c.transaction()) }
    }
    override fun findById(id: String): Transaction? = readableDatabase.query("transactions", null, "id = ?", arrayOf(id), null, null, null).use { c ->
        if (c.moveToFirst()) c.transaction() else null
    }
    override fun save(transaction: Transaction) {
        val values = ContentValues().apply {
            put("id", transaction.id); put("type", transaction.type.name); put("amount", transaction.amountMinor)
            put("date", transaction.date.toString()); put("category", transaction.category.name); put("description", transaction.description)
            put("odometer", transaction.odometerKm); put("liters", transaction.fuelLiters); put("receipt", transaction.receiptPath)
            put("latitude", transaction.latitude); put("longitude", transaction.longitude)
        }
        check(writableDatabase.insertWithOnConflict("transactions", null, values, SQLiteDatabase.CONFLICT_REPLACE) != -1L)
    }
    override fun delete(id: String) { check(writableDatabase.delete("transactions", "id = ?", arrayOf(id)) == 1) }
    private fun Cursor.transaction(): Transaction {
        fun text(name: String) = getString(getColumnIndexOrThrow(name))
        fun nullableLong(name: String): Long? = getColumnIndexOrThrow(name).let { if (isNull(it)) null else getLong(it) }
        fun nullableDouble(name: String): Double? = getColumnIndexOrThrow(name).let { if (isNull(it)) null else getDouble(it) }
        return Transaction(text("id"), TransactionType.valueOf(text("type")), getLong(getColumnIndexOrThrow("amount")),
            LocalDate.parse(text("date")), Category.valueOf(text("category")), text("description"),
            nullableLong("odometer"), text("liters"), text("receipt"), nullableDouble("latitude"), nullableDouble("longitude"))
    }
}
