package com.softbrain.hevix.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.softbrain.hevix.utils.SharedPref
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class WalletLedgerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWalletLedgerBinding

    private lateinit var context: Context
    private lateinit var activity: Activity
    private lateinit var userId: String
    private lateinit var fromDate: String
    private lateinit var toDate: String
    private lateinit var apiDateFormat: SimpleDateFormat
    private lateinit var dataList: ArrayList<WalletLedgerModel>
    private lateinit var ledgerAdapter: WalletLedgerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletLedgerBinding.inflate(layoutInflater)
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
        fromDate = apiDateFormat.format(fromDateCalender.time)
        toDate = apiDateFormat.format(fromDateCalender.time)

        binding.apply {
            tvFromDate.text = fromDate
            tvToDate.text = toDate

            fromDateLy.setOnClickListener({
                val datePickerDialog = DatePickerDialog(context, { _, year, month, dayOfMonth ->

                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)
                    fromDate = apiDateFormat.format(selectedDate.time)
                    tvFromDate.text = fromDate

                    getWalletLedger()

                }, currentYear, currentMonth, today)

                datePickerDialog.show()
            })

            toDateLy.setOnClickListener({
                val datePickerDialog = DatePickerDialog(context, { _, year, month, dayOfMonth ->

                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)
                    toDate = apiDateFormat.format(selectedDate.time)
                    tvToDate.text = toDate

                    getWalletLedger()

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


        getWalletLedger()

    }

    private fun filterData(searchKey: String) {
        val filteredList = ArrayList<WalletLedgerModel>()

        for (item in dataList) {
            if (item.billNo.contains(searchKey)) {
                filteredList.add(item)
            }
        }
        if (filteredList.isNotEmpty())
        {
            ledgerAdapter.filterData(filteredList)
        }

    }

    private fun getWalletLedger() {
        val progressDialog = ProgressDialog(context)
        progressDialog.setCancelable(false)
        progressDialog.show()
        progressDialog.setMessage("Please wait...")
        RetrofitClient.getInstance().api.getLedger(userId, fromDate, toDate)
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
                                dataList = ArrayList<WalletLedgerModel>()
                                for (position in 0 until transactionsArray.length()) {
                                    val transactionObject =
                                        transactionsArray.getJSONObject(position)
                                    val oldBalance = transactionObject.getString("OldBal")
                                    val amount = transactionObject.getString("Amount")
                                    val newBalance = transactionObject.getString("NewBal")
                                    val txnType = transactionObject.getString("TxnType")
                                    val remarks = transactionObject.getString("Remarks")
                                    var txnDate = transactionObject.getString("TxnDate")
                                    val crDrType = transactionObject.getString("Cr_Dr_Type")
                                    val billNo = transactionObject.getString("BillNo")

                                    txnDate = txnDate.split("T")[0]
                                    val walletLedgerModel = WalletLedgerModel(
                                        oldBalance, amount,
                                        newBalance, txnType, remarks, txnDate, crDrType, billNo
                                    )
                                    dataList.add(walletLedgerModel)
                                }

                                ledgerAdapter = WalletLedgerAdapter(dataList)
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