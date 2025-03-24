package com.softbrain.hevix.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.softbrain.hevix.databinding.LedgerItemBinding
import com.softbrain.hevix.databinding.StockLedgerItemBinding
import com.softbrain.hevix.models.StockLedgerModel
import com.softbrain.hevix.models.WalletLedgerModel

class StockLedgerAdapter(private var dataList: ArrayList<StockLedgerModel>) :
    RecyclerView.Adapter<StockLedgerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockLedgerViewHolder {
        val binding =
            StockLedgerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StockLedgerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: StockLedgerViewHolder, position: Int) {
        val ledgerModel = dataList[position]
        holder.bind(ledgerModel)
    }

    fun filterData(filteredList: ArrayList<StockLedgerModel>) {
        dataList = filteredList
        notifyDataSetChanged()
    }
}

class StockLedgerViewHolder(val binding: StockLedgerItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(ledgerModel: StockLedgerModel) {
        binding.apply {
            tvBillNo.text = ledgerModel.billNo
            tvDate.text = ledgerModel.txnDate
            tvOldStock.text = ledgerModel.oldStock
            tvStock.text = ledgerModel.stock
            tvNewStock.text = ledgerModel.newStock
            tvTxnType.text = ledgerModel.txnType
            tvRemarks.text = ledgerModel.remarks
        }
    }
}