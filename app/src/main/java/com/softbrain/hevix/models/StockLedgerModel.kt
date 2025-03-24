package com.softbrain.hevix.models

data class StockLedgerModel (
    val oldStock: String, val stock: String, val newStock: String, val txnType: String,
    val remarks: String, val txnDate: String, val crDrType: String, val billNo: String
)
