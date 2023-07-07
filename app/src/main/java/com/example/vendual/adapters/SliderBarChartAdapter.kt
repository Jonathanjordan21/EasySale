package com.example.vendual.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.vendual.BarChartCode
import com.example.vendual.Buyer
import com.example.vendual.fragments.BarChart

class SliderBarChartAdapter(fragment : FragmentActivity,
                            private val dataList :ArrayList<Buyer>,
                            private val merge : Boolean = false)
    : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = when(position){
        0 -> BarChart(BarChartCode.Buyer, dataList, merge)
        1 -> BarChart(BarChartCode.VendItem, dataList, merge)
        else -> BarChart(BarChartCode.Buyer, dataList, merge)
    }

}