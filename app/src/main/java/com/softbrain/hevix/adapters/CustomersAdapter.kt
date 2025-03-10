package com.softbrain.hevix.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.softbrain.hevix.databinding.CustomerItemBinding
import com.softbrain.hevix.models.CustomerModel

class CustomersAdapter(
    private val customerList: ArrayList<CustomerModel>,private val clickListener: (CustomerModel) -> Unit) : RecyclerView.Adapter<CustomerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding=CustomerItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CustomerViewHolder(binding)
    }

    override fun getItemCount(): Int {
       return customerList.size
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
       val customerModel=customerList[position]
        holder.bind(customerModel,clickListener)
    }
}

class CustomerViewHolder(private val binding:CustomerItemBinding) :RecyclerView.ViewHolder(binding.root)
{
    fun bind(customerModel:CustomerModel,clickListener: (CustomerModel) -> Unit)
    {
        binding.apply {
            tvName.text=customerModel.name
            tvPhone.text=customerModel.phone


            root.setOnClickListener({
                clickListener(customerModel)
            })
        }
    }
}