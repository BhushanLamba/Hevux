package com.softbrain.hevix.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.softbrain.hevix.R
import com.softbrain.hevix.adapters.PendingBillAdapter
import com.softbrain.hevix.adapters.WalletLedgerAdapter
import com.softbrain.hevix.databinding.ActivityWalletLedgerBinding
import com.softbrain.hevix.models.PendingBillsModel
import com.softbrain.hevix.models.WalletLedgerModel
import com.softbrain.hevix.network.RetrofitClient
import org.json.JSONObject

class WalletLedgerActivity : AppCompatActivity() {

    private lateinit var binding:ActivityWalletLedgerBinding

    private lateinit var context: Context
    private lateinit var activity: Activity
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityWalletLedgerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this
        activity = this
        userId = "1"


        getWalletLedger()

    }

    private fun getWalletLedger() {
        val progressDialog = ProgressDialog(context)
        progressDialog.setCancelable(false)
        progressDialog.show()
        progressDialog.setMessage("Please wait...")
        RetrofitClient.getInstance().api.getLedger(userId, "2025-03-01","2025-03-01")
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
                                val dataList = ArrayList<WalletLedgerModel>()
                                for (position in 0 until transactionsArray.length()) {
                                    val transactionObject = transactionsArray.getJSONObject(position)
                                    val oldBalance = transactionObject.getString("OldBal")
                                    val amount = transactionObject.getString("Amount")
                                    val newBalance = transactionObject.getString("NewBal")
                                    val txnType = transactionObject.getString("TxnType")
                                    val remarks = transactionObject.getString("Remarks")
                                    val txnDate = transactionObject.getString("TxnDate")
                                    val crDrType = transactionObject.getString("Cr_Dr_Type")
                                    val billNo = transactionObject.getString("BillNo")

                                    val walletLedgerModel = WalletLedgerModel(oldBalance,amount,
                                        newBalance,txnType,remarks,txnDate,crDrType,billNo)
                                    dataList.add(walletLedgerModel)
                                }

                                val ledgerAdapter = WalletLedgerAdapter(dataList)
                                val layoutManager = LinearLayoutManager(
                                    context,
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                                binding.ledgerRecycler.adapter = ledgerAdapter
                                binding.ledgerRecycler.layoutManager = layoutManager

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