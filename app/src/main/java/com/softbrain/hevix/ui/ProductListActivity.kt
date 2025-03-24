package com.softbrain.hevix.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.softbrain.hevix.adapters.ProductsAdapter
import com.softbrain.hevix.databinding.ActivityProductListBinding
import com.softbrain.hevix.models.ProductModel
import com.softbrain.hevix.network.RetrofitClient
import com.softbrain.hevix.utils.SharedPref
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductListBinding

    private lateinit var context: Context
    private lateinit var activity: Activity
    private lateinit var userId: String
    private lateinit var customerId: String

    private lateinit var customerName: String
    private lateinit var customerMobile: String
    private lateinit var customerAddress: String
    private lateinit var customerArea: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        context = this
        activity = this
        userId = SharedPref.getString(context, SharedPref.USER_ID).toString()

        customerId = intent.getStringExtra("customerId").toString()
        customerName = intent.getStringExtra("customerName").toString()
        customerMobile = intent.getStringExtra("customerMobile").toString()
        customerAddress = intent.getStringExtra("customerAddress").toString()
        customerArea = intent.getStringExtra("customerArea").toString()
        getProducts()

        binding.btnCart.setOnClickListener({
            val intent = Intent(activity, CartActivity::class.java)
            intent.putExtra("customerId", customerId)
            intent.putExtra("customerName", customerName)
            intent.putExtra("customerMobile", customerMobile)
            intent.putExtra("customerAddress", customerAddress)
            intent.putExtra("customerArea", customerArea)
            startActivity(intent)
            finish()
        })

        binding.imgBack.setOnClickListener({
            finish()
        })

    }

    private fun getProducts() {
        val progressDialog = ProgressDialog(context)
        progressDialog.setCancelable(false)
        progressDialog.show()
        progressDialog.setMessage("Please wait...")
        RetrofitClient.getInstance().api.getProducts(userId)
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
                                val dataList = ArrayList<ProductModel>()
                                for (position in 0 until transactionsArray.length()) {
                                    val transactionObject =
                                        transactionsArray.getJSONObject(position)
                                    val name = transactionObject.getString("ProductName")
                                    val id = transactionObject.getString("ID")
                                    val stock = transactionObject.getString("Stock")

                                    val productModel = ProductModel(name, id,stock)
                                    dataList.add(productModel)
                                }

                                val productsAdapter =
                                    ProductsAdapter(dataList) { selectedProduct: ProductModel, qty: String ->
                                        selectProduct(selectedProduct, qty)
                                    }
                                val layoutManager = LinearLayoutManager(
                                    context,
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                                binding.productsRecycler.adapter = productsAdapter
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

    private fun selectProduct(selectedProduct: ProductModel, qty: String) {
        val productId = selectedProduct.productId
        val productName = selectedProduct.productName

        addToCart(productId, productName, qty)


    }

    private fun addToCart(productId: String, productName: String, qty: String) {
        val progressDialog = ProgressDialog(context)
        progressDialog.setCancelable(false)
        progressDialog.show()
        progressDialog.setMessage("Please wait...")
        RetrofitClient.getInstance().api.addToCart(productId, productName, customerId, userId, qty)
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
                                    .setPositiveButton("Ok",null)
                                    .setCancelable(false)
                                    .show()


                                /*val transactionsArray = responseObject.getJSONArray("transactions")
                                val dataList = ArrayList<ProductModel>()
                                for (position in 0 until transactionsArray.length()) {
                                    val transactionObject =
                                        transactionsArray.getJSONObject(position)
                                    val name = transactionObject.getString("ProductName")
                                    val id = transactionObject.getString("Id")

                                    val productModel = ProductModel(name, id)
                                    dataList.add(productModel)
                                }

                                val productsAdapter =
                                    ProductsAdapter(dataList) { selectedProduct: ProductModel,qty:String ->
                                        selectProduct(selectedProduct,qty)
                                    }
                                val layoutManager = LinearLayoutManager(
                                    context,
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                                binding.productsRecycler.adapter = productsAdapter
                                binding.productsRecycler.layoutManager = layoutManager*/

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