package com.softbrain.hevix.models

data class WalletLedgerModel(
    val oldBalance: String, val amount: String, val newBalance: String, val txnType: String,
    val remarks: String, val txnDate: String, val crDrType: String, val billNo: String
)
