package com.softbrain.hevix.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.softbrain.hevix.databinding.PaymentDetailsItemBinding
import com.softbrain.hevix.models.PaymentDetailsModel

class PaymentDetailsAdapter(private val dataList:ArrayList<PaymentDetailsModel>) : RecyclerView.Adapter<PaymentDetailsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentDetailsViewHolder {
        val binding=PaymentDetailsItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PaymentDetailsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: PaymentDetailsViewHolder, position: Int) {
        val model=dataList[position]
        holder.bind(model)
    }

}
class PaymentDetailsViewHolder(private val binding:PaymentDetailsItemBinding) : RecyclerView.ViewHolder(binding.root)
{
    fun bind(paymentDetailsModel: PaymentDetailsModel)
    {
        binding.apply {
            tvBillNo.text=paymentDetailsModel.billNo
            tvAmount.text=paymentDetailsModel.amount
            tvPaymentMode.text=paymentDetailsModel.paymentMode
            tvDate.text=paymentDetailsModel.date
        }
    }
}