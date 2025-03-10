package com.softbrain.hevix.models

data class PendingBillsModel(val customerName:String, val mobileNumber:String, val address:String,
                             val totalAmount:String, val receivedAmount:String, val balanceAmount:String, val billDate:String)
