package com.softbrain.hevix.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.softbrain.hevix.databinding.PendingBillsItemBinding
import com.softbrain.hevix.models.PendingBillsModel

class PendingBillAdapter(private val dataList: ArrayList<PendingBillsModel>) :
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
        holder.bind(pendingBillsModel)
    }
}

class PendingBillsViewHolder(private val binding: PendingBillsItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("SetTextI18n")
    fun bind(pendingBillsModel: PendingBillsModel) {
        val customerName = pendingBillsModel.customerName
        val phone = pendingBillsModel.mobileNumber
        val totalAmount = pendingBillsModel.totalAmount
        val receivedAmount = pendingBillsModel.receivedAmount
        val balanceAmount = pendingBillsModel.balanceAmount
        val address = pendingBillsModel.address


        binding.apply {
            tvName.text = customerName
            tvPhone.text = phone
            tvTotalAmount.text = "₹ $totalAmount"
            tvReceivedAmount.text = "₹ $receivedAmount"
            tvBalanceAmount.text = "₹ $balanceAmount"
            tvAddress.text = address
        }
    }
}