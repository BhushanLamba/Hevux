package com.softbrain.hevix.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.softbrain.hevix.R
import com.softbrain.hevix.adapters.CustomersAdapter
import com.softbrain.hevix.databinding.ActivityAddCustomerBinding
import com.softbrain.hevix.models.CustomerModel
import com.softbrain.hevix.network.RetrofitClient
import com.softbrain.hevix.utils.Constants
import com.softbrain.hevix.utils.SharedPref
import org.json.JSONObject

class AddCustomerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddCustomerBinding
    private lateinit var context: Context
    private lateinit var activity: Activity
    private lateinit var userId: String
    private lateinit var statesList: ArrayList<String>
    private lateinit var weekDaysList: ArrayList<String>
    private lateinit var areaList: ArrayList<String>
    private lateinit var areaIdList: ArrayList<String>
    private lateinit var state: String
    private lateinit var area: String
    private lateinit var areaId: String
    private lateinit var weekDay: String
    private lateinit var shopName: String
    private lateinit var customerName: String
    private lateinit var emailId: String
    private lateinit var phone: String
    private lateinit var fullAddress: String
    private lateinit var pinCode: String
    private lateinit var remarks: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this
        activity = this
        userId = SharedPref.getString(context, SharedPref.USER_ID).toString()

        setUpViews()

        binding.apply {
            btnAdd.setOnClickListener({
                shopName = etShopName.text.toString().trim()
                customerName = etCustomerName.text.toString().trim()
                emailId = etEmailId.text.toString().trim()
                phone = etPhone.text.toString().trim()
                fullAddress = etAddress.text.toString().trim()
                pinCode = etPinCode.text.toString().trim()
                remarks = etRemarks.text.toString().trim()

                addUser()
            })
        }

    }

    private fun addUser() {
        val progressDialog = ProgressDialog(context)
        progressDialog.setCancelable(false)
        progressDialog.show()
        progressDialog.setMessage("Please wait...")
        RetrofitClient.getInstance().api.addCustomer(
            shopName,
            customerName,
            emailId,
            phone,
            weekDay,
            fullAddress,
            pinCode,
            state,
            area,
            areaId,
            remarks
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

                            val message = responseObject.getString("response_msg")

                            AlertDialog.Builder(context)
                                .setMessage(message)
                                .setPositiveButton("OK", object : DialogInterface.OnClickListener {
                                    override fun onClick(dialog: DialogInterface?, which: Int) {
                                        finish()
                                    }

                                })
                                .show()

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

    private fun setUpViews() {
        statesList = Constants.statesList
        weekDaysList = Constants.getWeekDaysList()

        val stateAdapter =
            ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, statesList)
        val weekDaysAdapter =
            ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, weekDaysList)

        binding.apply {
            spinnerState.adapter = stateAdapter
            spinnerState.onItemSelectedListener =
                object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

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
                        state = statesList[position]

                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }


            spinnerDays.adapter = weekDaysAdapter
            spinnerDays.onItemSelectedListener =
                object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
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
                        getArea()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }

        }

    }

    private fun getArea() {
        val progressDialog = ProgressDialog(context)
        progressDialog.setCancelable(false)
        progressDialog.show()
        progressDialog.setMessage("Please wait...")
        RetrofitClient.getInstance().api.getArea(userId, weekDay)
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
                                areaList = ArrayList()
                                areaIdList = ArrayList()
                                for (position in 0 until transactionsArray.length()) {
                                    val transactionObject =
                                        transactionsArray.getJSONObject(position)
                                    val areaId = transactionObject.getString("ID")
                                    val area = transactionObject.getString("AreaName")

                                    areaList.add(area)
                                    areaIdList.add(areaId)
                                }

                                binding.apply {
                                    val areaAdapter = ArrayAdapter(
                                        context,
                                        android.R.layout.simple_spinner_dropdown_item,
                                        areaList
                                    )

                                    spinnerArea.adapter = areaAdapter
                                    spinnerArea.onItemSelectedListener =
                                        object : AdapterView.OnItemClickListener,
                                            AdapterView.OnItemSelectedListener {

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
                                                area = areaList[position]
                                                areaId = areaIdList[position]

                                            }

                                            override fun onNothingSelected(parent: AdapterView<*>?) {}
                                        }
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
}