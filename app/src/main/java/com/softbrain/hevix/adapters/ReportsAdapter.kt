package com.softbrain.hevix.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.softbrain.hevix.databinding.ReportItemBinding
import com.softbrain.hevix.models.ReportModel

class ReportsAdapter(private var dataList: ArrayList<ReportModel>) :RecyclerView.Adapter<ReportsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportsViewHolder {
        val binding=ReportItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ReportsViewHolder(binding)
    }

    override fun getItemCount(): Int {
       return dataList.size
    }

    override fun onBindViewHolder(holder: ReportsViewHolder, position: Int) {
        val reportsModel=dataList[position]
        holder.bind(reportsModel)
    }

    fun filterData(filteredList: ArrayList<ReportModel>) {
        dataList=filteredList
        notifyDataSetChanged()

    }
}

class ReportsViewHolder(val binding: ReportItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(reportModel: ReportModel) {
        binding.apply {
            tvName.text = reportModel.name
            tvMobile.text = reportModel.phone
            tvStatus.text = reportModel.status
            tvDate.text = reportModel.date
            tvReceivedAmount.text = reportModel.receivedAmount
            tvAmount.text = reportModel.amount
            tvBalanceAmount.text = reportModel.balanceAmount
            tvAddress.text = reportModel.address
            tvBillNo.text = reportModel.billNo
        }
    }
}