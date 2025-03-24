package com.softbrain.hevix.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.softbrain.hevix.databinding.LedgerItemBinding
import com.softbrain.hevix.models.WalletLedgerModel

class WalletLedgerAdapter(private var dataList: ArrayList<WalletLedgerModel>) :
    RecyclerView.Adapter<WalletLedgerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletLedgerViewHolder {
        val binding = LedgerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WalletLedgerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: WalletLedgerViewHolder, position: Int) {
        val ledgerModel = dataList[position]
        holder.bind(ledgerModel)
    }

    fun filterData(filteredList: ArrayList<WalletLedgerModel>) {
        dataList=filteredList
        notifyDataSetChanged()
    }

}

class WalletLedgerViewHolder(val binding: LedgerItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(ledgerModel: WalletLedgerModel) {
        binding.apply {
            tvBillNo.text = ledgerModel.billNo
            tvDate.text = ledgerModel.txnDate
            tvOldBalance.text = ledgerModel.oldBalance
            tvAmount.text = ledgerModel.amount
            tvNewBalance.text = ledgerModel.newBalance
            tvTxnType.text = ledgerModel.txnType
            tvRemarks.text = ledgerModel.remarks
        }
    }
}