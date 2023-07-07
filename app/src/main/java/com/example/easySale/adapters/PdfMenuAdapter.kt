package com.example.easySale.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.easySale.Items
import com.example.easySale.R

class PdfMenuAdapter(private val context: Context,
                     private val items: ArrayList<Items>)
: RecyclerView.Adapter<PdfMenuAdapter.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfMenuAdapter.ViewHolder =
        ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.pdf_menu_item, parent, false)
        )




    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var name = items[position].title
        var price = items[position].price.toString()
        if(name.length > 10){
            name = items[position].title
                .replaceRange(10, name.length, "...")
        }

        when {
            price.toFloat() >= 1_000_000_000 -> {
                price = "${(price.toFloat()/ 1_000_000_000)}B"
            }
            price.toFloat() >= 1_000_000 -> {
                price = "${(price.toFloat()/ 1_000_000)}M"
            }
            price.toFloat() >= 1000 -> {
                price = "${(price.toFloat()/ 1000)}K"
            }
        }

        holder.tvLabel.text = name
        holder.tvPrice.text = price

    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val tvLabel: TextView = itemView.findViewById(R.id.tvPdfMenuItemLabel)
        val tvPrice : TextView = itemView.findViewById(R.id.tvPdfMenuItemPrice)

    }


}