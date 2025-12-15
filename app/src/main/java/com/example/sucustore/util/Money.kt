package com.example.sucustore.util

import java.text.NumberFormat
import java.util.Locale

object Money {
    private val clp = Locale("es", "CL")

    fun clp(amount: Double): String {
        val nf = NumberFormat.getCurrencyInstance(clp)
        nf.maximumFractionDigits = 0
        nf.minimumFractionDigits = 0
        return nf.format(amount)
    }
}
