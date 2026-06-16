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
import com.softbrain.hevix.adapters.ProductsAdapter
import com.softbrain.hevix.adapters.StockSummaryAdapter
import com.softbrain.hevix.databinding.ActivityProductListBinding
import com.softbrain.hevix.databinding.ActivityStockSummaryBinding
import com.softbrain.hevix.models.ProductModel
import com.softbrain.hevix.models.StockSummaryModel
import com.softbrain.hevix.network.RetrofitClient
import com.softbrain.hevix.utils.SharedPref
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class StockSummaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStockSummaryBinding
    private lateinit var context: Context
    private lateinit var activity: Activity
    private lateinit var userId: String
    private lateinit var apiDateFormat: SimpleDateFormat
    private lateinit var showDateFormat: SimpleDateFormat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        context = this
        activity = this
        userId = SharedPref.getString(context, SharedPref.USER_ID).toString()
        apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        showDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        binding.imgBack.setOnClickListener { finish() }
        getStockSummary()
    }

    private fun getStockSummary() {
        val progressDialog = ProgressDialog(context)
        progressDialog.setCancelable(false)
        progressDialog.show()
        progressDialog.setMessage("Please wait...")
        RetrofitClient.getInstance().api.getStockSummary(userId)
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
                                val dataList = ArrayList<StockSummaryModel>()
                                for (position in 0 until transactionsArray.length()) {
                                    val transactionObject =
                                        transactionsArray.getJSONObject(position)
                                    val name = transactionObject.getString("ProductName")
                                    val price = transactionObject.getString("Price")
                                    val totalStock = transactionObject.getString("TotalStock")
                                    val availableStock =
                                        transactionObject.getString("AvailableStock")

                                    val model =
                                        StockSummaryModel(name, price, totalStock, availableStock)
                                    dataList.add(model)
                                }

                                val adapter =
                                    StockSummaryAdapter(dataList)
                                val layoutManager = LinearLayoutManager(
                                    context,
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                                binding.productsRecycler.adapter = adapter
                                binding.productsRecycler.layoutManager = layoutManager

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