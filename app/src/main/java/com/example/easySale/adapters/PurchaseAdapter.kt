package com.example.easySale.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.easySale.*

class PurchaseAdapter(private val context: Context, private val sumList: ArrayList<Summary>)
    : RecyclerView.Adapter<PurchaseAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchaseAdapter.ViewHolder =
        ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.purchase_pdf_item, parent, false)
        )


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = sumList[position].name
        holder.quantity.text = sumList[position].quantity.toString()
        holder.price.text = "Rp ${sumList[position].price}"
        holder.priceQuantity.text= "Rp ${sumList[position].price*sumList[position].quantity}"
    }


    override fun getItemCount(): Int = sumList.size

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val name : TextView = itemView.findViewById(R.id.tvItemName)
        val quantity : TextView = itemView.findViewById(R.id.tvQuantity)
        val price : TextView = itemView.findViewById(R.id.tvPrice)
        val priceQuantity : TextView = itemView.findViewById(R.id.tvPriceQuantity)
    }


}