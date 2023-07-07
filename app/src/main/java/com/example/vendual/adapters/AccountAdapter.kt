package com.example.vendual.adapters

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.vendual.*
import com.example.vendual.activities.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import java.util.*
import kotlin.collections.ArrayList

class AccountAdapter(private val context: Activity, private val acc: ArrayList<String>)
    : RecyclerView.Adapter<AccountAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountAdapter.ViewHolder =
        ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.account_item, parent, false)
        )


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //val tinyDB = TinyDB(context)
        holder.name.text = acc[position]
        holder.cvAccount.setOnClickListener {
            TinyDB(context).putString(CURRENT_WALLET, acc[position])
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
            context.finish()

        }



    }


    override fun getItemCount(): Int = acc.size

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.findViewById(R.id.tvAccountName)
        val cvAccount: CardView= itemView.findViewById(R.id.cvAccount)
//        val total: TextView = itemView.findViewById<TextView>(R.id.tvAccountTotal)
    }


}