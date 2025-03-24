package com.softbrain.hevix.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.softbrain.hevix.adapters.ReturnProductAdapter
import com.softbrain.hevix.databinding.ActivityReturnProductBinding
import com.softbrain.hevix.models.BillDetailsModel
import com.softbrain.hevix.network.RetrofitClient
import com.softbrain.hevix.utils.SharedPref
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReturnProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReturnProductBinding
    private lateinit var context: Context
    private lateinit var activity: Activity
    private lateinit var userId: String
    private lateinit var billNo: String
    private lateinit var dataList: ArrayList<BillDetailsModel>
    private lateinit var customerId:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReturnProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this
        activity = this
        userId = SharedPref.getString(context, SharedPref.USER_ID).toString()


        binding.apply {

            imgBack.setOnClickListener({
                finish()
            })


            btnSearch.setOnClickListener {
                billNo = etSearch.text.toString().trim()

                if (!TextUtils.isEmpty(billNo)) {
                    getBillDetails()
                } else {
                    etSearch.error = "Required"
                }
            }

            btnCart.setOnClickListener({
                val intent = Intent(activity, ReturnCartActivity::class.java)
                intent.putExtra("customerId", customerId)
                intent.putExtra("billNo", billNo)
                startActivity(intent)
                finish()
            })
        }

    }

    private fun getBillDetails() {
        val progressDialog = ProgressDialog(context)
        progressDialog.setCancelable(false)
        progressDialog.show()
        progressDialog.setMessage("Please wait...")
        RetrofitClient.getInstance().api.getBillDetails(userId, billNo)
            .enqueue(object : Callback<JsonObject> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val responseObject = JSONObject(response.body().toString())

                            val responseCode = responseObject.getString("response_code")
                            val message = responseObject.getString("response_msg")
                            if (responseCode.equals("TXN", ignoreCase = true)) {
                                val orderDetailsArray = responseObject.getJSONArray("orderdetails")
                                dataList = ArrayList()
                                for (position in 0 until orderDetailsArray.length()) {
                                    val orderDetailsObject =
                                        orderDetailsArray.getJSONObject(position)
                                    val productName = orderDetailsObject.getString("ProductName")
                                    val productId = orderDetailsObject.getString("ProductId")
                                    customerId = orderDetailsObject.getString("UserId")
                                    val quantity = orderDetailsObject.getString("Qnt")
                                    val price = orderDetailsObject.getString("Price")
                                    val total = orderDetailsObject.getString("Total")
                                    val status = orderDetailsObject.getString("Status")

                                    val billDetailsModel = BillDetailsModel(
                                        productName,
                                        quantity,
                                        price,
                                        total,
                                        status, productId, customerId
                                    )
                                    dataList.add(billDetailsModel)
                                }

                                binding.btnCart.visibility=View.VISIBLE


                                binding.recycler.layoutManager =
                                    LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                                binding.recycler.adapter =
                                    ReturnProductAdapter(dataList) { orderDetailsModel: BillDetailsModel, quantity: String ->
                                        addToReturnCart(orderDetailsModel, quantity)
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

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    progressDialog.dismiss()
                    Toast.makeText(context, t.localizedMessage, Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun addToReturnCart(orderDetailsModel: BillDetailsModel, quantity: String) {

        val progressDialog = ProgressDialog(context)
        progressDialog.setCancelable(false)
        progressDialog.show()
        progressDialog.setMessage("Please wait...")
        RetrofitClient.getInstance().api.addToReturnCart(orderDetailsModel.productId,
            orderDetailsModel.productName,
            customerId,userId,billNo,quantity)
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        try {
                            val responseObject = JSONObject(response.body().toString())

                            val responseCode = responseObject.getString("response_code")
                            val message = responseObject.getString("response_msg")
                            if (responseCode.equals("TXN", ignoreCase = true)) {


                                AlertDialog.Builder(context)
                                    .setMessage(message)
                                    .setPositiveButton("Ok", null)
                                    .setCancelable(false)
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

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    progressDialog.dismiss()
                    Toast.makeText(context, t.localizedMessage, Toast.LENGTH_LONG).show()
                }

            })
    }
}