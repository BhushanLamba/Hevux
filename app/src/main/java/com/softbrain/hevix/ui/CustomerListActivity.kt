package com.softbrain.hevix.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.softbrain.hevix.adapters.CustomersAdapter
import com.softbrain.hevix.databinding.ActivityCustomerListBinding
import com.softbrain.hevix.models.CustomerModel
import com.softbrain.hevix.network.RetrofitClient
import com.softbrain.hevix.utils.Constants
import com.softbrain.hevix.utils.SharedPref
import org.json.JSONObject

class CustomerListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomerListBinding
    private lateinit var context: Context
    private lateinit var activity: Activity
    private lateinit var userId: String
    private lateinit var weekDaysList: ArrayList<String>
    private lateinit var weekDay: String
    private lateinit var type: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this
        activity = this
        userId = SharedPref.getString(context, SharedPref.USER_ID).toString()
        type = intent.getStringExtra("type").toString()

        weekDaysList = Constants.getWeekDaysList()
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, weekDaysList)
        binding.apply {
            spinnerDays.adapter = adapter
            spinnerDays.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
                override fun onItemClick(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    weekDay = weekDaysList[position]
                    getCustomers()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            imgBack.setOnClickListener({
                finish()
            })
        }

    }

    private fun getCustomers() {
        val progressDialog = ProgressDialog(context)
        progressDialog.setCancelable(false)
        progressDialog.show()
        progressDialog.setMessage("Please wait...")
        RetrofitClient.getInstance().api.getCustomers(userId, weekDay)
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
                                val dataList = ArrayList<CustomerModel>()
                                for (position in 0 until transactionsArray.length()) {
                                    val transactionObject =
                                        transactionsArray.getJSONObject(position)
                                    val name = transactionObject.getString("CustomerName")
                                    val phone = transactionObject.getString("Phone")
                                    val id = transactionObject.getString("ID")
                                    val address = transactionObject.getString("FullAddress")
                                    val area = transactionObject.getString("Area")

                                    val customerModel =
                                        CustomerModel(name, phone, id, address, area)
                                    dataList.add(customerModel)
                                }

                                val customerAdapter = CustomersAdapter(dataList,{ selectedCustomer: CustomerModel -> selectCustomer(selectedCustomer) },type)
                                val layoutManager = LinearLayoutManager(
                                    context,
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                                binding.customersRecycler.adapter = customerAdapter
                                binding.customersRecycler.layoutManager = layoutManager

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

    private fun selectCustomer(customerModel: CustomerModel) {
        if (type.equals("SALES_PRODUCT", true)) {
            val customerId = customerModel.id
            val intent = Intent(activity, ProductListActivity::class.java)
            intent.putExtra("customerId", customerId)
            intent.putExtra("customerName", customerModel.name)
            intent.putExtra("customerMobile", customerModel.phone)
            intent.putExtra("customerAddress", customerModel.address)
            intent.putExtra("customerArea", customerModel.area)
            startActivity(intent)
            finish()
        }
    }
}