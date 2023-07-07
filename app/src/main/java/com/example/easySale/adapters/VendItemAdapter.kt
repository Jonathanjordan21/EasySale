package com.example.easySale.adapters

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.easySale.*
import java.util.*
import kotlin.collections.ArrayList


class VendItemAdapter(private val context : Context, private var vendItems : MutableList<Items>,
                      private var activity : FragmentActivity,
                      private var revenueAdapter : ViewPager2? = null)
    : RecyclerView.Adapter<VendItemAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.vend_item, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vendItem = vendItems[position]

        var price = vendItem.price.toString()
        when {
            price.toFloat() >= 1_000_000_000 -> {
                price = "${(price.toFloat()/ 1_000_000_000)} B"
            }
            price.toFloat() >= 1_000_000 -> {
                price = "${(price.toFloat()/ 1_000_000)} M"
            }
            price.toFloat() >= 1000 -> {
                price = "${(price.toFloat()/ 1000)} K"
            }
        }

        var sold = vendItem.sold.toString()
        when {
            sold.toFloat() >= 1_000_000_000 -> {
                sold = "${(sold.toFloat()/ 1_000_000_000)} B"
            }
            sold.toFloat() >= 1_000_000 -> {
                sold = "${(sold.toFloat()/ 1_000_000)} M"
            }
            sold.toFloat() >= 1000 -> {
                sold = "${(sold.toFloat()/ 1000)} K"
            }
        }

        holder.title.text = vendItem.title
        holder.price.text = price.replace('.', ',')
        holder.sold.text = sold.replace('.', ',')

        vendItem.label.let {
            if (it.isNullOrEmpty()) {
                //holder.labelBg.setBackgroundColor(Color.WHITE)
                holder.labelBg.background.setTint(Color.WHITE)
                holder.labelName.text = "No Category"
                holder.labelName.setTextColor(Color.BLACK)
                holder.labelImage.setColorFilter(Color.BLACK)
                holder.labelImage.setImageResource(R.drawable.ic_baseline_label_24)

            } else {
//                holder.labelBg.setBackgroundColor(it[0].backgroundColor)
                holder.labelBg.background.setTint(it[0].backgroundColor)
                holder.labelName.text = it[0].name
                holder.labelName.setTextColor(Color.BLACK)
                holder.labelImage.setColorFilter(Color.BLACK)
                holder.labelImage.setImageDrawable(
                    vendItem.label!![0].icon.getDrawableByNameAssets(context))
            }
        }

        val dialog = Dialog(context)
        val tinyDB = TinyDB(context)
        val buyerList = tinyDB.getAutoListObject<Buyer>()

        //holder.image.setImageResource(vendItem.image)

        holder.itemView.setOnClickListener{
            Toast.makeText(context, vendItem.title, Toast.LENGTH_SHORT).show()
        }

        holder.btnEdit?.editNewVendItems(
            context = context,
            vendItems = vendItems,
            position = position,
            code = VendButtonCode.EDIT_VEND_ITEM,
            adapter = this
        )


        holder.btnDelete?.setOnClickListener {
            dialog.setContentView(R.layout.popup_delete)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

            val confirm = dialog.findViewById<Button>(R.id.btnConfirmDelete)
            val cancel = dialog.findViewById<Button>(R.id.btnCancelDelete)

            dialog.findViewById<TextView>(R.id.tvDelete).text = vendItem.title

            confirm.setOnClickListener {

                buyerList.forEach {
                        if(it.itemName == vendItem.title) it.isItemDeleted = true
                    }


                vendItems.removeAt(position)

                tinyDB.putListObject(VEND_DB.currentAcc(tinyDB),
                    vendItems as ArrayList<Any>)
                notifyDataSetChanged()
                //notifyItemRemoved(position)
                dialog.dismiss()
            }
            cancel.setOnClickListener {
                dialog.dismiss()
            }
        }

        holder.btnSales?.setOnClickListener {
            dialog.setContentView(R.layout.popup_sales)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

            val confirm = dialog.findViewById<Button>(R.id.btnConfirmSales)
            val cancel = dialog.findViewById<TextView>(R.id.cancelIconSales)
            val extCustName = dialog.findViewById<EditText>(R.id.etxtCustName)
            val etQuantitiy = dialog.findViewById<EditText>(R.id.etQuantity)



            confirm.setOnClickListener {
                val custName =
                    if (extCustName.text.isNullOrBlank()) {
                        context.resources.getString(R.string.unknown)
                    } else {
                        extCustName.text.toString().lowercase()
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                    }


                etQuantitiy.text.toString().apply{
                    if(isNotBlank() && toInt() != 0){
                        val date = Date()
                        val buyer = Buyer(custName, toInt(), vendItem.title,
                            vendItem.price, date, vendItem.label)

                        buyerList.add(buyer)
                        tinyDB.putListObject(BUYER_DB.currentAcc(tinyDB),
                            buyerList as ArrayList<Any>)

                        vendItem.sold += toInt()
                        tinyDB.putListObject(VEND_DB.currentAcc(tinyDB),
                            vendItems as ArrayList<Any>)
                        notifyDataSetChanged()
                        dialog.dismiss()
                        revenueAdapter?.adapter = SliderRevenueAdapter(activity
                            ,tinyDB.getAutoListObject())


                    } else {
                        Toast
                            .makeText(context, "Quantity cannot be 0!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            }

            cancel.setOnClickListener {
                dialog.dismiss()
            }
        }

    }

    override fun getItemCount(): Int = vendItems.size


    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
       // var image : ImageView = itemView.findViewById(R.id.image)
        val title : TextView = itemView.findViewById(R.id.title)
        val price : TextView = itemView.findViewById(R.id.price)
        val sold : TextView = itemView.findViewById(R.id.tvSold)
        val labelBg : CardView= itemView.findViewById(R.id.cvVendItem)
        val labelName : TextView = itemView.findViewById(R.id.labelVendItem)
        val labelImage : ImageView = itemView.findViewById(R.id.imageLabel)


        val btnEdit: Button?  = itemView.findViewById(R.id.btnEdit)
        val btnDelete: Button?  = itemView.findViewById(R.id.btnDelete)
        val btnSales: Button? = itemView.findViewById(R.id.btnSale)

    }


}