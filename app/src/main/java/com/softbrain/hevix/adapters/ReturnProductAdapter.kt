package com.softbrain.hevix.adapters

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.softbrain.hevix.databinding.ReturnProductItemBinding
import com.softbrain.hevix.models.BillDetailsModel

class ReturnProductAdapter(
    private val dataList: ArrayList<BillDetailsModel>,
    private val
    addToReturnCart: (BillDetailsModel,String) -> Unit
) : RecyclerView.Adapter<ReturnProductViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReturnProductViewHolder {
        val binding = ReturnProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReturnProductViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ReturnProductViewHolder, position: Int) {
        val billDetailsModel = dataList[position]
        holder.bind(billDetailsModel,addToReturnCart)
    }
}
class ReturnProductViewHolder(val binding: ReturnProductItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("SetTextI18n")
    fun bind(billDetailsModel: BillDetailsModel,addToReturnCart: (BillDetailsModel,String) -> Unit) {
        binding.apply {
            tvProduct.text = billDetailsModel.productName
            tvQuantity.text = billDetailsModel.quantity
            tvPrice.text = "₹ ${billDetailsModel.price}"
            tvTotal.text = "₹ ${billDetailsModel.total}"

            btnAdd.setOnClickListener({
                val quantity=etQuantity.text.toString().trim()
                if (!TextUtils.isEmpty(quantity))
                {
                    addToReturnCart(billDetailsModel,quantity)
                }
                else
                {
                    etQuantity.error="Required"
                }
            })
        }
    }
}