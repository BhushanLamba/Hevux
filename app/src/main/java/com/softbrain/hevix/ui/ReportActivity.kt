package com.softbrain.hevix.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.softbrain.hevix.adapters.ReportsAdapter
import com.softbrain.hevix.databinding.ActivityReportBinding
import com.softbrain.hevix.models.ReportModel
import com.softbrain.hevix.models.WalletLedgerModel
import com.softbrain.hevix.network.RetrofitClient
import com.softbrain.hevix.utils.Constants
import com.softbrain.hevix.utils.SharedPref
import org.json.JSONObject

class ReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportBinding
    private lateinit var context: Context
    private lateinit var activity: Activity
    private lateinit var userId: String
    private lateinit var statusList: ArrayList<String>
    private lateinit var status: String
    private lateinit var dataList: ArrayList<ReportModel>
    private lateinit var adapter: ReportsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        context = this
        activity = this
        userId = SharedPref.getString(context, SharedPref.USER_ID).toString()

        statusList = Constants.getStatusList()
        val adapter =
            ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, statusList)
        binding.apply {
            spinnerStatus.adapter = adapter
            spinnerStatus.onItemSelectedListener = object :
                AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
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
                    status = statusList[position]
                    getReport()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

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
        val filteredList = ArrayList<ReportModel>()

        for (item in dataList) {
            if (item.billNo.contains(searchKey)) {
                filteredList.add(item)
            }
        }
        if (filteredList.isNotEmpty())
        {
            adapter.filterData(filteredList)
        }

    }

    private fun getReport() {
        val progressDialog = ProgressDialog(context)
        progressDialog.setCancelable(false)
        progressDialog.show()
        progressDialog.setMessage("Please wait...")
        RetrofitClient.getInstance().api.getReport(userId, status)
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

                                    val name = transactionObject.getString("CustomerName")
                                    val phone = transactionObject.getString("MobileNo")
                                    val address = transactionObject.getString("Address")
                                    val amount = transactionObject.getString("TotalAmt")
                                    val receivedAmount = transactionObject.getString("ReceivedAmt")
                                    val balanceAmount = transactionObject.getString("BalanceAmt")
                                    var date = transactionObject.getString("BillDate")
                                    val status = transactionObject.getString("PaymentStatus")
                                    val billNo = transactionObject.getString("Id")


                                    date = date.split("T")[0]

                                    val reportsModel = ReportModel(
                                        name, phone, status, date, receivedAmount,
                                        amount, balanceAmount, address,billNo
                                    )

                                    dataList.add(reportsModel)
                                }

                                adapter =
                                    ReportsAdapter(dataList)
                                val layoutManager = LinearLayoutManager(
                                    context,
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                                binding.reportRecycler.adapter = adapter
                                binding.reportRecycler.layoutManager = layoutManager


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