package com.softbrain.hevix.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.softbrain.hevix.databinding.OrderDetailsItemBinding
import com.softbrain.hevix.models.BillDetailsModel

class BillDetailsAdapter(private val dataList: ArrayList<BillDetailsModel>) :
    RecyclerView.Adapter<BillDetailsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillDetailsViewHolder {
        val binding =
            OrderDetailsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BillDetailsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: BillDetailsViewHolder, position: Int) {
        val billDetailsModel = dataList[position]
        holder.bind(billDetailsModel)
    }

}

class BillDetailsViewHolder(val binding: OrderDetailsItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("SetTextI18n")
    fun bind(billDetailsModel: BillDetailsModel) {
        binding.apply {
            tvProduct.text = billDetailsModel.productName
            tvQuantity.text = billDetailsModel.quantity
            tvPrice.text = "₹ ${billDetailsModel.price}"
            tvTotal.text = "₹ ${billDetailsModel.total}"
            tvStatus.text = billDetailsModel.status
        }
    }
}