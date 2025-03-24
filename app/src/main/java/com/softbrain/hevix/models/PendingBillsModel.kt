package com.softbrain.hevix.models

data class PendingBillsModel(val id:String,val customerName:String, val mobileNumber:String, val address:String,
                             val totalAmount:String, val receivedAmount:String, val balanceAmount:String, val billDate:String)
