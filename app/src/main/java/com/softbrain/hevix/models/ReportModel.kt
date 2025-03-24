package com.softbrain.hevix.models

data class ReportModel(
    val name: String,
    val phone: String,
    val status: String,
    val date: String,
    val receivedAmount: String,
    val amount: String,
    val balanceAmount: String,
    val address: String,
    val billNo: String
)