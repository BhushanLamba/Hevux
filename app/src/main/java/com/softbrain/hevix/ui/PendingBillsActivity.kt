package com.softbrain.hevix.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.softbrain.hevix.adapters.PendingBillAdapter
import com.softbrain.hevix.databinding.ActivityPendingBillsBinding
import com.softbrain.hevix.models.PendingBillsModel
import com.softbrain.hevix.network.RetrofitClient
import org.json.JSONObject

class PendingBillsActivity : AppCompatActivity() {

    private lateinit var binding:ActivityPendingBillsBinding
    private lateinit var context: Context
    private lateinit var activity: Activity
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityPendingBillsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this
        activity = this
        userId = "1"

        getPendingBills()

    }

    private fun getPendingBills() {
        val progressDialog = ProgressDialog(context)
        progressDialog.setCancelable(false)
        progressDialog.show()
        progressDialog.setMessage("Please wait...")
        RetrofitClient.getInstance().api.getPendingBill(userId, "2025-03-01")
            .enqueue(object : retrofit2.Callback<JsonObject> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: retrofit2.Call<JsonObject>,
                    response: retrofit2.Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val responseObject = JSONObject(response.body().toString())

                            val responseCode = responseObject.getString("response_code")
                            val message = responseObject.getString("response_msg")
                            if (responseCode.equals("TXN", ignoreCase = true)) {
                                val transactionsArray = responseObject.getJSONArray("transactions")
                                val dataList = ArrayList<PendingBillsModel>()
                                for (position in 0 until transactionsArray.length()) {
                                    val transactionObject = transactionsArray.getJSONObject(position)
                                    val customerName = transactionObject.getString("CustomerName")
                                    val phone = transactionObject.getString("MobileNo")
                                    val address = transactionObject.getString("Address")
                                    val totalAmount = transactionObject.getString("TotalAmt")
                                    val receivedAmount = transactionObject.getString("ReceivedAmt")
                                    val balanceAmount = transactionObject.getString("BalanceAmt")
                                    val billDate = transactionObject.getString("BillDate")

                                    val pendingBillModel = PendingBillsModel(customerName,phone,address, totalAmount, receivedAmount, balanceAmount, billDate)
                                    dataList.add(pendingBillModel)
                                }

                                val billAdapter = PendingBillAdapter(dataList)
                                val layoutManager = LinearLayoutManager(
                                    context,
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                                binding.billsRecycler.adapter = billAdapter
                                binding.billsRecycler.layoutManager = layoutManager

                            } else {
                                AlertDialog.Builder(context)
                                    .setMessage(message)
                                    .setPositiveButton("OK", null)
                                    .show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show()
                    }
                    progressDialog.dismiss()
                }

                override fun onFailure(call: retrofit2.Call<JsonObject>, t: Throwable) {
                    progressDialog.dismiss()
                    Toast.makeText(context, t.localizedMessage, Toast.LENGTH_LONG).show()
                }
            })
    }
}