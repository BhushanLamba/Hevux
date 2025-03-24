package com.softbrain.hevix.models

data class BillDetailsModel(
    val productName: String, val quantity: String,
    val price: String, val total: String,
    val status: String, val productId: String,
    val userId: String
)
