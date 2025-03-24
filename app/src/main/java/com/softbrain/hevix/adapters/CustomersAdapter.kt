package com.softbrain.hevix.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.softbrain.hevix.databinding.CustomerItemBinding
import com.softbrain.hevix.models.CustomerModel

class CustomersAdapter(
    private val customerList: ArrayList<CustomerModel>,private val clickListener: (CustomerModel) -> Unit,private val type:String) : RecyclerView.Adapter<CustomerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding=CustomerItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CustomerViewHolder(binding)
    }

    override fun getItemCount(): Int {
       return customerList.size
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
       val customerModel=customerList[position]
        holder.bind(customerModel,clickListener,type)
    }
}

class CustomerViewHolder(private val binding:CustomerItemBinding) :RecyclerView.ViewHolder(binding.root)
{
    fun bind(customerModel:CustomerModel,clickListener: (CustomerModel) -> Unit,type:String)
    {
        binding.apply {
            tvName.text=customerModel.name
            tvPhone.text=customerModel.phone
            tvArea.text=customerModel.area
            tvAddress.text=customerModel.address

            if (type.equals("SALES_PRODUCT", true)) {
                areaLy.visibility=View.GONE
                addressLy.visibility=View.GONE
            }


                root.setOnClickListener({
                clickListener(customerModel)
            })
        }
    }
}