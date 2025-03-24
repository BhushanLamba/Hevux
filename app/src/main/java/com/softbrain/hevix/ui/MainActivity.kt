package com.softbrain.hevix.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.service.quicksettings.PendingIntentActivityWrapper
import com.google.gson.JsonObject
import com.softbrain.hevix.databinding.ActivityMainBinding
import com.softbrain.hevix.databinding.SelectSalesProductDialogBinding
import com.softbrain.hevix.network.RetrofitClient
import com.softbrain.hevix.utils.SharedPref
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var context: Context
    private lateinit var activity: Activity
    private lateinit var userId: String
    private lateinit var name: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this
        activity = this
        userId = SharedPref.getString(context, SharedPref.USER_ID).toString()
        name = SharedPref.getString(context, SharedPref.NAME).toString()


        clickEvents()

    }

    @SuppressLint("SetTextI18n")
    private fun clickEvents() {
        binding.apply {

            tvName.text = "Hello $name"

            salesProductLy.setOnClickListener({
                showCustomerDialog()
            })

            pendingBillLy.setOnClickListener({
                startActivity(Intent(activity, PendingBillsActivity::class.java))
            })

            ledgerLy.setOnClickListener({
                startActivity(Intent(activity, WalletLedgerActivity::class.java))
            })

            reportLy.setOnClickListener({
                startActivity(Intent(activity, ReportActivity::class.java))
            })

            todayVisitLy.setOnClickListener({
                val intent = Intent(activity, CustomerListActivity::class.java)
                intent.putExtra("type", "TODAY_VISIT")
                startActivity(intent)
            })

            returnProductLy.setOnClickListener({
                startActivity(Intent(activity,ReturnProductActivity::class.java))
            })

            stockLedgerLy.setOnClickListener({
                startActivity(Intent(activity, StockLedgerActivity::class.java))
            })
        }
    }

    private fun showCustomerDialog() {
        val binding = SelectSalesProductDialogBinding.inflate(LayoutInflater.from(activity))

        val salesProductDialogBuilder = AlertDialog.Builder(activity)

        salesProductDialogBuilder.setView(binding.root)

        val salesProductDialog = salesProductDialogBuilder.create()

        salesProductDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        salesProductDialog.setCancelable(false)
        salesProductDialog.show()




        binding.apply {
            selectCustomerLy.setOnClickListener({
                salesProductDialog.dismiss()
                val intent = Intent(activity, CustomerListActivity::class.java)
                intent.putExtra("type", "SALES_PRODUCT")
                startActivity(intent)
            })

            addNewLy.setOnClickListener({
                salesProductDialog.dismiss()
                startActivity(Intent(activity, AddCustomerActivity::class.java))
            })


        }
    }

    override fun onResume() {
        super.onResume()
        getBalance()
    }

    private fun getBalance() {
        val progressDialog = ProgressDialog(context)
        progressDialog.setCancelable(false)
        progressDialog.show()
        progressDialog.setMessage("Please wait...")
        RetrofitClient.getInstance().api.getBalance(userId)
            .enqueue(object : Callback<JsonObject> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        try {
                            val responseObject = JSONObject(response.body().toString())

                            val responseCode = responseObject.getString("response_code")
                            val message = responseObject.getString("response_msg")
                            if (responseCode.equals("TXN", ignoreCase = true)) {
                                val transactionsArray = responseObject.getJSONArray("transactions")
                                val transactionObject = transactionsArray.getJSONObject(0)
                                val balance = transactionObject.getString("Balance")

                                binding.tvBalance.text = "₹ $balance"
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

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    progressDialog.dismiss()
                    Toast.makeText(context, t.localizedMessage, Toast.LENGTH_LONG).show()
                }
            })
    }
}