package com.softbrain.hevix.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.softbrain.hevix.databinding.CartItemBinding
import com.softbrain.hevix.models.CartListModel

class CartAdapter(private val dataList: ArrayList<CartListModel>,private val deleteItem: (CartListModel) -> Unit) :
    RecyclerView.Adapter<CartViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartModel = dataList[position]
        holder.bind(cartModel,deleteItem)
    }
}

class CartViewHolder(private val binding: CartItemBinding) : RecyclerView.ViewHolder(binding.root) {
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