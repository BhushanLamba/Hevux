package com.softbrain.hevix.adapters

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.softbrain.hevix.databinding.ProductsItemBinding
import com.softbrain.hevix.models.ProductModel

class ProductsAdapter(
    private val dataList: ArrayList<ProductModel>, private val
    clickListener: (ProductModel,String) -> Unit
) : RecyclerView.Adapter<ProductsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val binding =
            ProductsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductsViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val productModel = dataList[position]
        holder.bind(productModel, clickListener)
    }
}

class ProductsViewHolder(private val binding: ProductsItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(productModel: ProductModel, clickListener: (ProductModel,String) -> Unit) {
        binding.apply {
            tvProductName.text = productModel.productName

            tvAdd.setOnClickListener({
                val qty = etQty.text.toString().trim()

                if (!TextUtils.isEmpty(qty)) {
                    clickListener(productModel,qty)
                }

            })
        }
    }
}