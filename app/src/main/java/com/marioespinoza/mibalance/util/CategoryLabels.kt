package com.marioespinoza.mibalance.util

import com.marioespinoza.mibalance.R
import com.marioespinoza.mibalance.domain.model.Category

val Category.labelRes: Int get() = when (this) {
    Category.DRIVING -> R.string.cat_driving
    Category.OTHER_WORK -> R.string.cat_other_work
    Category.OTHER_INCOME -> R.string.cat_other_income
    Category.FUEL -> R.string.cat_fuel
    Category.REPAIRS -> R.string.cat_repairs
    Category.MAINTENANCE -> R.string.cat_maintenance
    Category.FOOD -> R.string.cat_food
    Category.LEISURE -> R.string.cat_leisure
    Category.PERSONAL -> R.string.cat_personal
    Category.OTHER_EXPENSE -> R.string.cat_other_expense
}
