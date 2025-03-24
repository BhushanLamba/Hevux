package com.softbrain.hevix.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.JsonObject
import com.softbrain.hevix.R
import com.softbrain.hevix.databinding.ActivityPayPendingBillBinding
import com.softbrain.hevix.databinding.ActivityPendingBillsBinding
import com.softbrain.hevix.network.RetrofitClient
import com.softbrain.hevix.utils.SharedPref
import org.json.JSONObject

class PayPendingBillActivity : AppCompatActivity() {
    private lateinit var binding:ActivityPayPendingBillBinding
    private lateinit var context: Context
    private lateinit var activity: Activity
    private lateinit var userId: String
    private lateinit var receivedAmount: String
    private lateinit var remarks: String
    private lateinit var billNo: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityPayPendingBillBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this
        activity = this
        userId = SharedPref.getString(context, SharedPref.USER_ID).toString()
        billNo=intent.getStringExtra("billNo").toString()

        binding.apply {
            btnPay.setOnClickListener({
                receivedAmount = etReceivedAmount.text.toString().trim()
                remarks = etReceivedAmount.text.toString().trim()

                payPendingBills()
            })
        }


    }

    private fun payPendingBills() {
        val progressDialog = ProgressDialog(context)
        progressDialog.setCancelable(false)
        progressDialog.show()
        progressDialog.setMessage("Please wait...")
        RetrofitClient.getInstance().api.payPendingBalance(userId,"CASH",
            billNo,remarks,receivedAmount)
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

                                AlertDialog.Builder(context)
                                    .setMessage(message)
                                    .setCancelable(false)
                                    .setPositiveButton("OK"
                                    ) { _, _ ->
                                        startActivity(Intent(activity, ReportActivity::class.java))
                                        finish()
                                    }
                                    .show()

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