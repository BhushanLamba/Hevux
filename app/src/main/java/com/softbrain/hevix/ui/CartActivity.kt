package com.softbrain.hevix.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.softbrain.hevix.adapters.CartAdapter
import com.softbrain.hevix.databinding.ActivityCartBinding
import com.softbrain.hevix.models.CartListModel
import com.softbrain.hevix.network.RetrofitClient
import org.json.JSONObject

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var context: Context
    private lateinit var activity: Activity
    private lateinit var userId: String
    private lateinit var customerId: String
    private lateinit var customerName: String
    private lateinit var customerMobile: String
    private lateinit var customerAddress: String
    private lateinit var customerArea: String

    private lateinit var receivedAmount: String
    private lateinit var remarks: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this
        activity = this
        userId = "1"
        customerId = intent.getStringExtra("customerId").toString()
        customerName = intent.getStringExtra("customerName").toString()
        customerMobile = intent.getStringExtra("customerMobile").toString()
        customerAddress = intent.getStringExtra("customerAddress").toString()
        customerArea = intent.getStringExtra("customerArea").toString()

        getCartList()

        binding.apply {
            btnProceed.setOnClickListener({

                cartRecycler.visibility = View.GONE
                btnProceed.visibility = View.GONE
                detailsLy.visibility = View.VISIBLE
            })


            btnBookOrder.setOnClickListener({
                receivedAmount = etReceivedAmount.text.toString().trim()
                remarks = etReceivedAmount.text.toString().trim()

                bookOrder()
            })
        }

    }

    private fun bookOrder() {
        val progressDialog = ProgressDialog(context)
        progressDialog.setCancelable(false)
        progressDialog.show()
        progressDialog.setMessage("Please wait...")
        RetrofitClient.getInstance().api.bookOrder(
            customerId, userId, "CASH", receivedAmount, customerName, customerAddress,
            customerMobile, customerArea, remarks
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
                                    .setPositiveButton("OK", null)
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
        RetrofitClient.getInstance().api.getCart(customerId, userId)
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

                                val cartAdapter = CartAdapter(dataList) { cartItem: CartListModel ->
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

        RetrofitClient.getInstance().api.deleteCartItem(cartId, userId)
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