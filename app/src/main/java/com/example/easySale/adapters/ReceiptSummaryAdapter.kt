package com.example.easySale.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.easySale.*

class ReceiptSummaryAdapter(private val context: Context,
                            private val list: ArrayList<Summary>)
    : RecyclerView.Adapter<ReceiptSummaryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptSummaryAdapter.ViewHolder =
        ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.summary_receipt_item, parent, false)
        )


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = list[position].name.shortenToDots(19)
        holder.price.text = "Rp ${(list[position].price).toBigDecimal().toPlainString()}"
        holder.quantity.text = "x${list[position].quantity}"

    }


    override fun getItemCount(): Int = list.size

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.findViewById<TextView>(R.id.tvItemName)
        val price: TextView = itemView.findViewById<TextView>(R.id.tvPrice)
        val quantity: TextView = itemView.findViewById<TextView>(R.id.tvQuantity)
    }


}