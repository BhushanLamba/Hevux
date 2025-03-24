package com.softbrain.hevix.ui

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.softbrain.hevix.R
import com.softbrain.hevix.adapters.BillDetailsAdapter
import com.softbrain.hevix.adapters.PaymentDetailsAdapter
import com.softbrain.hevix.databinding.ActivityBillDetailsBinding
import com.softbrain.hevix.models.BillDetailsModel
import com.softbrain.hevix.models.PaymentDetailsModel
import com.softbrain.hevix.utils.SharedPref
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.io.OutputStream
import java.util.Objects

class BillDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBillDetailsBinding
    private lateinit var context: Context
    private lateinit var activity: Activity
    private lateinit var userId: String
    private lateinit var dataList: ArrayList<BillDetailsModel>
    private lateinit var paymentDetailsList: ArrayList<PaymentDetailsModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBillDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this
        activity = this
        userId = SharedPref.getString(context, SharedPref.USER_ID).toString()
        getSetData()

        binding.apply {
            shareLy.setOnClickListener {
                val bitmap: Bitmap = takeScreenshot(
                    binding.receiptLy,
                    binding.receiptLy.getChildAt(0).height,
                    binding.receiptLy.getChildAt(0).width
                )
                val uri = bitmapToFile(bitmap)
                shareReceipt(uri, false)
            }

            whatsAppLy.setOnClickListener {
                val bitmap: Bitmap = takeScreenshot(
                    binding.receiptLy,
                    binding.receiptLy.getChildAt(0).height,
                    binding.receiptLy.getChildAt(0).width
                )
                val uri = bitmapToFile(bitmap)
                shareReceipt(uri, true)
            }

            tvCustomerDetails.setOnClickListener {
                if (customerDetailsLy.visibility == View.VISIBLE) {
                    customerDetailsLy.visibility = View.GONE
                } else {
                    customerDetailsLy.visibility = View.VISIBLE
                }
            }

            tvOrderDetails.setOnClickListener {
                if (orderDetailsRecycler.visibility == View.VISIBLE) {
                    orderDetailsRecycler.visibility = View.GONE
                } else {
                    orderDetailsRecycler.visibility = View.VISIBLE
                }
            }


            tvPayementDetails.setOnClickListener {
                if (paymentDetailsRecycler.visibility == View.VISIBLE) {
                    paymentDetailsRecycler.visibility = View.GONE
                } else {
                    paymentDetailsRecycler.visibility = View.VISIBLE
                }
            }

        }

    }

    private fun getSetData() {
        try {
            val responseStr = intent.getStringExtra("response").toString()
            val responseObject = JSONObject(responseStr)

            val transactionObject = responseObject.getJSONArray("transactions").getJSONObject(0)

            val customerName = transactionObject.getString("CustomerName")
            val mobileNo = transactionObject.getString("MobileNo")
            val address = transactionObject.getString("Address")
            val area = transactionObject.getString("Area")
            val totalAmount = transactionObject.getString("TotalAmt")
            val receivedAmount = transactionObject.getString("ReceivedAmt")
            val balanceAmount = transactionObject.getString("BalanceAmt")
            var billDate = transactionObject.getString("BillDate")
            val paymentStatus = transactionObject.getString("PaymentStatus")
            billDate = billDate.split("T")[0] + "," + billDate.split("T")[1]


            val orderDetailsArray = responseObject.getJSONArray("orderdetails")
            dataList = ArrayList()
            for (position in 0 until orderDetailsArray.length()) {
                val orderDetailsObject = orderDetailsArray.getJSONObject(position)
                val productName = orderDetailsObject.getString("ProductName")
                val quantity = orderDetailsObject.getString("Qnt")
                val price = orderDetailsObject.getString("Price")
                val total = orderDetailsObject.getString("Total")
                val status = orderDetailsObject.getString("Status")
                val productId = orderDetailsObject.getString("ProductId")
                val customerId = orderDetailsObject.getString("UserId")

                val billDetailsModel = BillDetailsModel(productName, quantity, price, total, status,productId,customerId)
                dataList.add(billDetailsModel)
            }

            val paymentDetailsArray = responseObject.getJSONArray("Paymentdetails")
            paymentDetailsList = ArrayList()
            for (position in 0 until paymentDetailsArray.length()) {
                val paymentDetailsObject = paymentDetailsArray.getJSONObject(position)
                val billNo = paymentDetailsObject.getString("BillNo")
                val amount = paymentDetailsObject.getString("Amount")
                val paymentMode = paymentDetailsObject.getString("PaymentMode")
                var date = paymentDetailsObject.getString("ReqDate")

                date = date.split("T")[0] + "," + date.split("T")[1]

                val paymentDetailsModel = PaymentDetailsModel(billNo, amount, paymentMode, date)
                paymentDetailsList.add(paymentDetailsModel)
            }






            binding.apply {
                tvCustomerName.text = customerName
                tvMobile.text = mobileNo
                tvAddress.text = address
                tvArea.text = area
                tvTotalAmount.text = totalAmount
                tvReceivedAmount.text = receivedAmount
                tvBalanceAmount.text = balanceAmount
                tvBillDate.text = billDate
                tvPaymentStatus.text = paymentStatus


                orderDetailsRecycler.layoutManager =LinearLayoutManager(context, RecyclerView.VERTICAL, false)

                orderDetailsRecycler.adapter = BillDetailsAdapter(dataList)


                paymentDetailsRecycler.layoutManager =LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                paymentDetailsRecycler.adapter = PaymentDetailsAdapter(paymentDetailsList)

            }

        } catch (ignore: Exception) {
        }
    }

    private fun takeScreenshot(view: ScrollView, height: Int, width: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)

        return bitmap
    }

    private fun bitmapToFile(imageBitmap: Bitmap): Uri? {
        val fos: OutputStream
        val contentResolver = activity.contentResolver
        val contentValues = ContentValues()
        contentValues.put(
            MediaStore.MediaColumns.DISPLAY_NAME,
            "Image" + System.currentTimeMillis() + ".jpg"
        )
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        contentValues.put(
            MediaStore.MediaColumns.RELATIVE_PATH,
            Environment.DIRECTORY_PICTURES + File.separator + "hevux"
        )
        val uri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        try {
            fos = contentResolver.openOutputStream(Objects.requireNonNull<Uri>(uri))!!
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            Objects.requireNonNull(fos)

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }



        Toast.makeText(context, "File Saved", Toast.LENGTH_LONG).show()

        return uri
    }

    private fun shareReceipt(uri: Uri?, isWhatsApp: Boolean) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        if (isWhatsApp) {
            intent.`package` = "com.whatsapp"
        }
        startActivity(intent)
    }

}