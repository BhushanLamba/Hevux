package com.softbrain.hevix.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.softbrain.hevix.databinding.PendingBillsItemBinding
import com.softbrain.hevix.models.PendingBillsModel

class PendingBillAdapter(
    private var dataList: ArrayList<PendingBillsModel>, private val
    payPendingBill: (PendingBillsModel) -> Unit,private val billDetails: (PendingBillsModel) -> Unit
) :
    RecyclerView.Adapter<PendingBillsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingBillsViewHolder {
        val binding =
            PendingBillsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PendingBillsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: PendingBillsViewHolder, position: Int) {
        val pendingBillsModel = dataList[position]
        holder.bind(pendingBillsModel, payPendingBill, billDetails)
    }

    fun filterData(filteredList: ArrayList<PendingBillsModel>) {
        dataList = filteredList
        notifyDataSetChanged()

    }
}

class PendingBillsViewHolder(private val binding: PendingBillsItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("SetTextI18n")
    fun bind(pendingBillsModel: PendingBillsModel, payPendingBill: (PendingBillsModel) -> Unit,
             billDetails:(PendingBillsModel) -> Unit) {
        val customerName = pendingBillsModel.customerName
        val phone = pendingBillsModel.mobileNumber
        val totalAmount = pendingBillsModel.totalAmount
        val receivedAmount = pendingBillsModel.receivedAmount
        val balanceAmount = pendingBillsModel.balanceAmount
        val address = pendingBillsModel.address
        val billNo = pendingBillsModel.id


        binding.apply {
            tvBillNo.text = billNo
            tvName.text = customerName
            tvPhone.text = phone
            tvTotalAmount.text = "₹ $totalAmount"
            tvReceivedAmount.text = "₹ $receivedAmount"
            tvBalanceAmount.text = "₹ $balanceAmount"
            tvAddress.text = address


            root.setOnClickListener {
                payPendingBill(pendingBillsModel)
            }

            tvViewDetails.setOnClickListener {
                billDetails(pendingBillsModel)
            }
        }
    }
}