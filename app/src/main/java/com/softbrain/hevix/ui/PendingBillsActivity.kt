package com.softbrain.hevix.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.softbrain.hevix.adapters.PendingBillAdapter
import com.softbrain.hevix.databinding.ActivityPendingBillsBinding
import com.softbrain.hevix.models.PendingBillsModel
import com.softbrain.hevix.network.RetrofitClient
import com.softbrain.hevix.utils.SharedPref
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PendingBillsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPendingBillsBinding
    private lateinit var context: Context
    private lateinit var activity: Activity
    private lateinit var userId: String
    private lateinit var apiDateFormat: SimpleDateFormat
    private lateinit var date: String
    private lateinit var dataList: ArrayList<PendingBillsModel>
    private lateinit var billAdapter: PendingBillAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPendingBillsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this
        activity = this
        userId = SharedPref.getString(context, SharedPref.USER_ID).toString()
        apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        val calender = Calendar.getInstance()
        val currentYear = calender.get(Calendar.YEAR)
        val currentMonth = calender.get(Calendar.MONTH)
        val today = calender.get(Calendar.DAY_OF_MONTH)

        val fromDateCalender = Calendar.getInstance()
        fromDateCalender.set(currentYear, currentMonth, today)
        date = apiDateFormat.format(fromDateCalender.time)
        getPendingBills()


        binding.apply {
            tvDate.text = date

            tvDate.setOnClickListener({
                val datePickerDialog = DatePickerDialog(context, { _, year, month, dayOfMonth ->

                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)
                    date = apiDateFormat.format(selectedDate.time)
                    tvDate.text = date
                    getPendingBills()

                }, currentYear, currentMonth, today)

                datePickerDialog.show()
            })

            imgBack.setOnClickListener({
                finish()
            })

            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val searchKey = s.toString()
                    filterData(searchKey)
                }

                override fun afterTextChanged(s: Editable?) {

                }

            })
        }


    }

    private fun filterData(searchKey: String) {
        val filteredList = ArrayList<PendingBillsModel>()

        for (item in dataList) {
            if (item.mobileNumber.contains(searchKey)) {
                filteredList.add(item)
            }
        }
        if (filteredList.isNotEmpty()) {
            billAdapter.filterData(filteredList)
        }

    }

    private fun getPendingBills() {
        val progressDialog = ProgressDialog(context)
        progressDialog.setCancelable(false)
        progressDialog.show()
        progressDialog.setMessage("Please wait...")
        RetrofitClient.getInstance().api.getPendingBill(userId, date)
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
                                dataList = ArrayList()
                                for (position in 0 until transactionsArray.length()) {
                                    val transactionObject =
                                        transactionsArray.getJSONObject(position)
                                    val id = transactionObject.getString("Id")
                                    val customerName = transactionObject.getString("CustomerName")
                                    val phone = transactionObject.getString("MobileNo")
                                    val address = transactionObject.getString("Address")
                                    val totalAmount = transactionObject.getString("TotalAmt")
                                    val receivedAmount = transactionObject.getString("ReceivedAmt")
                                    val balanceAmount = transactionObject.getString("BalanceAmt")
                                    var billDate = transactionObject.getString("BillDate")
                                    billDate = billDate.split("T")[0]

                                    val pendingBillModel = PendingBillsModel(
                                        id,
                                        customerName,
                                        phone,
                                        address,
                                        totalAmount,
                                        receivedAmount,
                                        balanceAmount,
                                        billDate
                                    )
                                    dataList.add(pendingBillModel)
                                }

                                billAdapter = PendingBillAdapter(dataList,
                                    { billModel: PendingBillsModel ->
                                        payPendingBill(billModel)
                                    },
                                    { billModel: PendingBillsModel -> getBillDetails(billModel) })
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

    private fun getBillDetails(billModel: PendingBillsModel) {
        val billNo = billModel.id
        val progressDialog = ProgressDialog(context)
        progressDialog.setCancelable(false)
        progressDialog.show()
        progressDialog.setMessage("Please wait...")
        RetrofitClient.getInstance().api.getBillDetails(userId, billNo)
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
                                val intent=Intent(activity,BillDetailsActivity::class.java)

                                intent.putExtra("response",responseObject.toString())
                                startActivity(intent)

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

    private fun payPendingBill(billModel: PendingBillsModel) {
        val billNo = billModel.id
        val intent = Intent(activity, PayPendingBillActivity::class.java)
        intent.putExtra("billNo", billNo)
        startActivity(intent)
    }


}