package com.example.easySale.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.easySale.*
import android.widget.ImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout


class BarChartAdapter(private val context: Context, private val dataList :ArrayList<Buyer>,
                      private val type: BarChartCode, private val merge: Boolean)
    : RecyclerView.Adapter<BarChartAdapter.ViewHolder>() {

    private val height : Int = 60f.pixelToDp(context)

    private val width : Int = 411f.pixelToDp(context)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(context)
                .inflate(R.layout.statistic_barchart_item, parent, false)
        )
    }



    private fun setupNormal(holder: ViewHolder, position: Int){
        if(type == BarChartCode.Buyer){
            val maxPrice = dataList.maxOf { it.quantity*it.costs }
            val data = dataList[position]
            val date = data.date
            holder.tvBarChartLabel.text = "| ${date.date}/${date.month+1}/${date.year+1900} |\n ${data.name}"
            holder.tvBarChartPrice.text = "Rp ${(data.costs * data.quantity).toBigDecimal().toPlainString().replace('.', ',')}"
            holder.tvBarChartBg.layoutParams = CoordinatorLayout
                .LayoutParams(
                    (width * data.costs*data.quantity / maxPrice).toInt(), height
                )
            if(!data.labels.isNullOrEmpty()) {
                holder.tvBarChartBg.background.setTint(data.labels!![0].backgroundColor)
                holder.ivBarChartLabelIcon.setImageDrawable(
                    data.labels!![0].icon.getDrawableByNameAssets(context)
                )
            } else {
                holder.tvBarChartBg.background.setTint(Color.GREEN)
                holder.ivBarChartLabelIcon.setImageResource(R.drawable.ic_baseline_label_24)
            }

        } else {
            val maxPrice = dataList.maxOf { it.quantity * it.costs }
            val data = dataList[position]
            val date = data.date
            holder.tvBarChartLabel.text = "| ${date.date}/${date.month+1}/${date.year+1900} |\n ${data.itemName}"
            holder.tvBarChartPrice.text = "Rp ${(data.costs * data.quantity).toBigDecimal().toPlainString().replace('.', ',')}"
            holder.tvBarChartBg.layoutParams = CoordinatorLayout
                .LayoutParams(
                    (width * data.costs * data.quantity / maxPrice).toInt(), height
                )
            if (!data.labels.isNullOrEmpty()) {
                holder.tvBarChartBg.background.setTint(data.labels!![0].backgroundColor)
                holder.ivBarChartLabelIcon.setImageDrawable(
                    data.labels!![0].icon.getDrawableByNameAssets(context)
                )
            } else {
                holder.tvBarChartBg.background.setTint(Color.GREEN)
                holder.ivBarChartLabelIcon.setImageResource(R.drawable.ic_baseline_label_24)
            }
        }

    }



    private fun setupMerge(holder: ViewHolder, position : Int){
        //val dataList2: HashMap<String, ArrayList<String>> = hashMapOf()

        if (type == BarChartCode.Buyer) {
            val dataList3 = dataList.groupingBy { it.name }.fold(0f) { init, v ->
                init + v.costs * v.quantity
            }.toList()
            val maxPrice = dataList3.maxOf { it.second }

            dataList3[position].run {
                holder.tvBarChartLabel.text = first
                holder.tvBarChartPrice.text = "Rp " + second.toBigDecimal().toPlainString().replace('.', ',')
                holder.tvBarChartBg.layoutParams = CoordinatorLayout
                    .LayoutParams((width * second / maxPrice).toInt(), height)
            }


            holder.tvBarChartBg.background.setTint(Color.WHITE)
            holder.ivBarChartLabelIcon.setImageResource(R.drawable.ic_baseline_account_circle_24)

        } else {
            val dataList3 = dataList.groupingBy { it.itemName }.fold(0f) { init, v ->
                init + v.costs * v.quantity
            }.toList()
            val maxPrice = dataList3.maxOf { it.second }

            dataList3[position].run {
                holder.tvBarChartLabel.text = first
                holder.tvBarChartPrice.text = "Rp " + second.toBigDecimal().toPlainString().replace('.', ',')
                holder.tvBarChartBg.layoutParams = CoordinatorLayout
                    .LayoutParams((width * second / maxPrice).toInt(), height)
            }

            holder.tvBarChartBg.background.setTint(Color.YELLOW)
            holder.ivBarChartLabelIcon.setImageResource(R.drawable.yellow_trophy)
        }


    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Pair first = costs * quantity
        //Pair second = labelBackgroundColor

        if(merge) setupMerge(holder, position) else setupNormal(holder, position)
        holder.tvBarChartLabel.text = holder.tvBarChartLabel.text
            .toString().shortenToDots(36)
    }

    override fun getItemCount(): Int =
        if(merge) {
            if (type == BarChartCode.Buyer) {
                dataList.distinctBy { it.name }.size
            } else {
                dataList.distinctBy { it.itemName }.size
            }
        } else dataList.size



    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val tvBarChartPrice: TextView = itemView.findViewById(R.id.tvBarChartPrice)
        val tvBarChartLabel: TextView = itemView.findViewById(R.id.tvBarChartLabel)
        val tvBarChartBg: TextView = itemView.findViewById(R.id.tvBarChartBg)
        val ivBarChartLabelIcon: ImageView = itemView.findViewById(R.id.ivBarChartLabelIcon)
        //val cvBarChartLabelIcon: CardView = itemView.findViewById(R.id.cvBarChartLabelIcon)

    }


}