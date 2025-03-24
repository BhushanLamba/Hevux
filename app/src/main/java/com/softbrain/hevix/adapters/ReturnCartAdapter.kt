package com.softbrain.hevix.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.softbrain.hevix.databinding.ReturnCartItemBinding
import com.softbrain.hevix.models.CartListModel

class ReturnCartAdapter(private val dataList: ArrayList<CartListModel>, private val deleteItem: ((CartListModel) -> Unit)) :
    RecyclerView.Adapter<ReturnCartViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReturnCartViewHolder {
        val binding =
            ReturnCartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReturnCartViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ReturnCartViewHolder, position: Int) {
        val cartModel = dataList[position]
        holder.bind(cartModel,deleteItem)
    }
}

class ReturnCartViewHolder(private val binding: ReturnCartItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("SetTextI18n")
    fun bind(cartListModel: CartListModel, deleteItem: (CartListModel) -> Unit) {
        binding.apply {
            tvProductName.text = cartListModel.productName
            tvPrice.text = "₹ ${cartListModel.price}"
            tvQty.text = cartListModel.qty
            tvTotal.text = "₹ ${cartListModel.total}"

            imgDelete.setOnClickListener({
                deleteItem(cartListModel)
            })
        }
    }
}