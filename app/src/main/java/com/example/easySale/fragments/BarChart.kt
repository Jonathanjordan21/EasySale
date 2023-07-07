package com.example.easySale.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.easySale.BarChartCode
import com.example.easySale.Buyer
import com.example.easySale.R
import com.example.easySale.adapters.BarChartAdapter



class BarChart(private val barChartCode: BarChartCode,
               private val dataList :ArrayList<Buyer>,
               private val merge : Boolean)
    : Fragment(R.layout.fragment_bar_chart) {

    private lateinit var rcv : RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rcv = requireView().findViewById(R.id.rvBarChart)
        rcv.layoutManager = GridLayoutManager(context, 1)

        rcv.adapter = BarChartAdapter(requireContext(), dataList, barChartCode, merge)

    }




}


































//        barChart = view.findViewById(R.id.barChart2)
//        //hBarChart = view.findViewById(R.id.barChart)
//
//        barList = ArrayList()
//        barList.add(BarEntry(0f, 500f))
//        barList.add(BarEntry(1f, 100f))
//        barList.add(BarEntry(2f, 200f))
//        barList.add(BarEntry(3f, 600f))
//        barList.add(BarEntry(4f, 800f))
//        barList.add(BarEntry(5f, 900f))
//        barList.add(BarEntry(6f, 300f))
//
//        val vall = arrayOf("ABC", "DEF", "GE", "HIB", "JKA", "LAA", "KO")
//
//
//        barDataSet = BarDataSet(barList, "Population")
//        barDataSet.valueTextSize = 21f
//        barDataSet.highLightColor = Color.WHITE
//
//        barData = BarData(barDataSet)
//        BarData()
//
//        barChart.data = barData
//        barData.barWidth = 12f
//
//        barChart.xAxis.valueFormatter = object : ValueFormatter(){
//            override fun getFormattedValue(value: Float): String {
//                return vall[value.toInt()]
//            }
//        }
//
//        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS, 250)
//        barDataSet.valueTextColor = Color.BLACK
//        barDataSet