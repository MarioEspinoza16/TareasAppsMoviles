package com.marioespinoza.mibalance

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Looper
import android.view.View
import com.marioespinoza.mibalance.domain.model.*
import com.marioespinoza.mibalance.ui.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.time.LocalDate

/** Real Android view rendering under Robolectric, not a physical-device screenshot. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], qualifiers = "es-rCR-w411dp-h891dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class AndroidRenderTest {
    @Test fun renderAllScreensForLayoutReview() {
        val app = RuntimeEnvironment.getApplication() as BalanceApplication
        val day = LocalDate.now()
        val maintenance = Transaction(type = TransactionType.EXPENSE, amountMinor = 500000, date = day, category = Category.MAINTENANCE, description = "Cambio de aceite y filtro", odometerKm = 80000, latitude = 9.9763, longitude = -84.8384)
        app.repository.save(Transaction(type = TransactionType.INCOME, amountMinor = 3000000, date = day, category = Category.DRIVING))
        app.repository.save(Transaction(type = TransactionType.EXPENSE, amountMinor = 800000, date = day, category = Category.FUEL))
        app.repository.save(maintenance)
        fun <T : BaseActivity> render(type: Class<T>, name: String, intent: Intent? = null) {
            val controller = if (intent == null) Robolectric.buildActivity(type) else Robolectric.buildActivity(type, intent)
            val activity = controller.setup().visible().get()
            repeat(10) { Thread.sleep(30); shadowOf(Looper.getMainLooper()).idle() }
            val root = activity.findViewById<View>(android.R.id.content)
            root.measure(View.MeasureSpec.makeMeasureSpec(822, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(1782, View.MeasureSpec.EXACTLY))
            root.layout(0, 0, 822, 1782)
            val bitmap = Bitmap.createBitmap(822, 1782, Bitmap.Config.ARGB_8888)
            root.draw(Canvas(bitmap))
            val target = File("build/reports/screen-previews/$name.png").apply { parentFile?.mkdirs() }
            target.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            bitmap.recycle(); controller.close()
        }
        render(DashboardActivity::class.java, "dashboard")
        render(HistoryActivity::class.java, "history")
        render(TransactionEditActivity::class.java, "editor", Intent(app, TransactionEditActivity::class.java).putExtra("transaction_id", maintenance.id))
        render(DetailActivity::class.java, "detail", Intent(app, DetailActivity::class.java).putExtra("transaction_id", maintenance.id))
        render(LocationActivity::class.java, "location", Intent(app, LocationActivity::class.java).putExtra("latitude", 9.9763).putExtra("longitude", -84.8384))
    }
}
