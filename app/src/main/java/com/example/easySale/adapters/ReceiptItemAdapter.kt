package com.example.easySale.adapters

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.easySale.*
import java.util.ArrayList

class ReceiptItemAdapter(private val context: Context,
                         private val summary: RecyclerView, private val textView: TextView)
    : RecyclerView.Adapter<ReceiptItemAdapter.ViewHolder>() {

    private val tinyDB = TinyDB(context)
    private val itemList : ArrayList<Items> = tinyDB.getAutoListObject<Items>()
    private val sumList = tinyDB.getAutoListObject<Summary>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptItemAdapter.ViewHolder =
        ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.receipt_item, parent, false)
        )


    override fun onBindViewHolder(holder: ViewHolder, position : Int) {
        holder.name.text = itemList[position].title
        holder.price.text = "Rp ${itemList[position].price.toString().replace('.', ',')}"

        var boolx = true

        itemList[position].label.let {
            if (it.isNullOrEmpty()) {
                //holder.labelBg.setBackgroundColor(Color.WHITE)
                holder.labelBg.background.setTint(Color.WHITE)
                holder.labelName.text = "No Category"
                holder.labelName.setTextColor(Color.BLACK)
                holder.labelImage.setColorFilter(Color.BLACK)
                holder.labelImage.setImageResource(R.drawable.ic_baseline_label_24)
                true
            } else {
//                holder.labelBg.setBackgroundColor(it[0].backgroundColor)
                holder.labelBg.background.setTint(it[0].backgroundColor)
                holder.labelName.text = it[0].name
                holder.labelName.setTextColor(Color.BLACK)
                holder.labelImage.setColorFilter(Color.BLACK)
                holder.labelImage.setImageDrawable(
                    itemList[position].label!![0].icon.getDrawableByNameAssets(context))
                true
            }
        }

        holder.quantity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                onTextChanged(s, start, count, after)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if(s == null) holder.quantity.setText("")

                else if(s.any { it == '-' || it =='.' ||
                            !it.isDigit() && it == ','}){
                    holder.quantity.setText("")
                    makeToast(context, "Invalid Quantity!")
                } else if(holder.quantity.text.isNotBlank() && boolx) {
                    val sum = Summary(
                        itemList[position].title,
                        holder.quantity.text.toString().trim().toInt(),
                        itemList[position].price,
                        itemList[position].label
                    )

                    var bool = true
                    for (i in sumList.indices) {
                        if (sumList[i].name == sum.name) {

                            sumList.removeAt(i)
                            sumList.add(i, sum)
                            bool = false
                            break
                        }
                    }

                    if(bool) sumList.add(sum)

                    reloadSumAdapter(position)
                }

            }
        })

        holder.btnPlus.setOnClickListener {
            boolx = true
            val x = if(holder.quantity.text.isNullOrBlank()) 0
            else holder.quantity.text.toString().toInt()
            holder.quantity.setText("${x+1}")

            val sum = Summary(
                itemList[position].title,
                holder.quantity.text.toString().toInt(),
                itemList[position].price,
                itemList[position].label
            )

            var bool = true
            for (i in sumList.indices) {
                if (sumList[i].name == sum.name) {

                    sumList.removeAt(i)
                    sumList.add(i, sum)
                    bool = false
                    break
                }
            }

            if(bool) sumList.add(sum)

            reloadSumAdapter(position)
        }

        holder.btnMinus.setOnClickListener {
            if(holder.quantity.text.isNullOrBlank() || holder.quantity.text.toString().toInt() == 0){
                makeToast(context, "Invalid Quantity!")
                summary.adapter = ReceiptSummaryAdapter(context, sumList)
            } else {
                boolx = false
                Log.d("test", "$${holder.quantity.text.toString().toInt()-1}")
                holder.quantity.setText("${holder.quantity.text.toString().toInt()-1}")

                val sum = Summary(
                    itemList[position].title,
                    holder.quantity.text.toString().toInt(),
                    itemList[position].price,
                    itemList[position].label
                )


                for (i in sumList.indices){
                    if(sumList[i].name == sum.name){
                        sumList.removeAt(i)
                        if(sum.quantity >= 1) {
                            sumList.add(i, sum)
                        }
                        break
                    }
                }

                Log.d("ISIM SUM LIST", "$sumList")


                reloadSumAdapter(position)
            }
        }


        holder.quantity.setOnClickListener {
            val imm =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            holder.quantity.isCursorVisible = if(!holder.quantity.isCursorVisible){
                true
            } else {
                imm.hideSoftInputFromWindow(holder.quantity.windowToken, 0)
                false
            }
        }


    }

    private fun reloadSumAdapter(position: Int){
        val totalV = sumList.fold(0f){
                init,v -> init + (v.price*v.quantity)
        }.toBigDecimal().toPlainString().replace('.', ',')

        if(sumList.isEmpty()) textView.text = "Total : Rp 0"
        else textView.text = "Total : Rp $totalV"
        tinyDB.putListObject(SUMMARY_DB.currentAcc(tinyDB), sumList as ArrayList<Any>)
        summary.adapter = ReceiptSummaryAdapter(context, sumList)
    }



    override fun getItemCount(): Int = itemList.size

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.findViewById<TextView>(R.id.title)
        val price: TextView = itemView.findViewById<TextView>(R.id.price)
        val quantity: EditText = itemView.findViewById(R.id.tvSold)
        val btnPlus: Button = itemView.findViewById(R.id.btnPlus)
        val btnMinus: Button = itemView.findViewById(R.id.btnMinus)

        val labelBg : CardView = itemView.findViewById(R.id.cvVendItem)
        val labelName : TextView = itemView.findViewById(R.id.labelVendItem)
        val labelImage : ImageView = itemView.findViewById(R.id.imageLabel)
    }


}