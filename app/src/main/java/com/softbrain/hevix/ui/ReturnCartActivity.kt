package com.softbrain.hevix.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.softbrain.hevix.adapters.CartAdapter
import com.softbrain.hevix.adapters.ReturnCartAdapter
import com.softbrain.hevix.databinding.ActivityReturnCartBinding
import com.softbrain.hevix.models.CartListModel
import com.softbrain.hevix.network.RetrofitClient
import com.softbrain.hevix.utils.SharedPref
import org.json.JSONObject

class ReturnCartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReturnCartBinding

    private lateinit var context: Context
    private lateinit var activity: Activity
    private lateinit var userId: String
    private lateinit var customerId: String
    private lateinit var billNo: String
    private lateinit var remarks: String
    private lateinit var loginUserName: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReturnCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this
        activity = this
        userId = SharedPref.getString(context, SharedPref.USER_ID).toString()
        customerId = intent.getStringExtra("customerId").toString()
        billNo = intent.getStringExtra("billNo").toString()
        loginUserName = SharedPref.getString(context, SharedPref.NAME).toString()

        getCartList()

        binding.apply {
            imgBack.setOnClickListener({
                finish()
            })

            btnProceed.setOnClickListener({

                cartRecycler.visibility = View.GONE
                btnProceed.visibility = View.GONE
                detailsLy.visibility = View.VISIBLE
            })

            btnBookOrder.setOnClickListener({
                remarks = etRemarks.text.toString().trim()
                bookOrder()
            })

        }

    }

    private fun bookOrder() {
        val progressDialog = ProgressDialog(context)
        progressDialog.setCancelable(false)
        progressDialog.show()
        progressDialog.setMessage("Please wait...")
        RetrofitClient.getInstance().api.bookReturnOrder(
            customerId, userId, loginUserName, billNo, remarks
        )
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
                                    .setPositiveButton(
                                        "OK",
                                        object : DialogInterface.OnClickListener {
                                            override fun onClick(
                                                dialog: DialogInterface?,
                                                which: Int
                                            ) {
                                                startActivity(
                                                    Intent(
                                                        activity,
                                                        ReportActivity::class.java
                                                    )
                                                )
                                                finish()
                                            }


                                        })
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


    private fun getCartList() {
        val progressDialog = ProgressDialog(context)
        progressDialog.setCancelable(false)
        progressDialog.show()
        progressDialog.setMessage("Please wait...")
        RetrofitClient.getInstance().api.getReturnCart(customerId, userId, billNo)
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
                                binding.tvTotal.text = "Total -: ₹ $message"
                                val transactionsArray = responseObject.getJSONArray("transactions")
                                val dataList = ArrayList<CartListModel>()
                                for (position in 0 until transactionsArray.length()) {
                                    val transactionObject =
                                        transactionsArray.getJSONObject(position)
                                    val id = transactionObject.getString("Id")
                                    val productName = transactionObject.getString("ProductName")
                                    val qty = transactionObject.getString("Qnt")
                                    val price = transactionObject.getString("Price")
                                    val total = transactionObject.getString("Total")
                                    val cartListModel =
                                        CartListModel(productName, qty, price, total, id)

                                    dataList.add(cartListModel)
                                }

                                val cartAdapter = ReturnCartAdapter(dataList){ cartItem: CartListModel ->
                                    deleteItem(cartItem)
                                }
                                val layoutManager = LinearLayoutManager(
                                    context,
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                                binding.apply {
                                    cartRecycler.adapter = cartAdapter
                                    cartRecycler.layoutManager = layoutManager
                                    btnProceed.visibility = View.VISIBLE
                                }


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

    private fun deleteItem(cartItem: CartListModel) {
        val progressDialog = ProgressDialog(context)
        progressDialog.setCancelable(false)
        progressDialog.show()
        progressDialog.setMessage("Please wait...")
        val cartId = cartItem.id

        RetrofitClient.getInstance().api.deleteReturnCartItem(cartId, userId)
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
                                    .setPositiveButton("OK", null)
                                    .show()
                                getCartList()

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