package com.softbrain.hevix.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.softbrain.hevix.databinding.StockLedgerItemBinding
import com.softbrain.hevix.databinding.StockSummaryItemBinding
import com.softbrain.hevix.models.StockSummaryModel

class StockSummaryAdapter(private val dataList: ArrayList<StockSummaryModel>) :
    RecyclerView.Adapter<StockSummaryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockSummaryViewHolder {
        val binding =
            StockSummaryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StockSummaryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: StockSummaryViewHolder, position: Int) {
        holder.bind(dataList[position])
    }
}

class StockSummaryViewHolder(private val binding: StockSummaryItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(model: StockSummaryModel) {
        binding.apply {
            tvProductName.text = model.productName
            tvPrice.text = model.price
            tvTotalStock.text = model.totalStock
            tvAvailableStock.text = model.availableStock
        }
    }
}