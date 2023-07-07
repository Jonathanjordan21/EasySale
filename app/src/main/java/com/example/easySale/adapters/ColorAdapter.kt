package com.example.easySale.adapters

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.easySale.LABEL_COLOR_DB
import com.example.easySale.LABEL_SAVING_COLOR
import com.example.easySale.R

class ColorAdapter (private val context: Context, private val colors: IntArray, private val dialog: Dialog)
    : RecyclerView.Adapter<ColorAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorAdapter.ViewHolder =
        ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.color_item, parent, false)
        )


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cvColor.apply{
            setCardBackgroundColor(colors[position])
        }.setOnClickListener{
            context.getSharedPreferences(LABEL_COLOR_DB, Context.MODE_PRIVATE).apply{
                edit().putInt(LABEL_SAVING_COLOR, colors[position]).apply()
            }
            dialog.cancel()
        }
    }


    override fun getItemCount(): Int = colors.size

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val cvColor : CardView = itemView.findViewById(R.id.cvColor)
    }


}