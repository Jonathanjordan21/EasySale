package com.example.easySale.adapters

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.easySale.*

class IconAdapter (private val context: Context, private val icons: ArrayList<String>, private val dialog: Dialog)
    : RecyclerView.Adapter<IconAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconAdapter.ViewHolder =
        ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.icon_item, parent, false)
        )


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.ivIcon.setImageDrawable(icons[position].getDrawableByNameAssets(context))
        holder.cvIcon.setOnClickListener{
            TinyDB(context).putObject(ICON_LABELS, icons[position])
            dialog.cancel()
        }
    }


    override fun getItemCount(): Int = icons.size

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val ivIcon : ImageView = itemView.findViewById(R.id.ivLabelItemIcon)
        val cvIcon : CardView = itemView.findViewById(R.id.cvLabelItemIcon)
    }


}