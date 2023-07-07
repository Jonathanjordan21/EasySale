package com.example.easySale.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.easySale.BarChartCode
import com.example.easySale.Buyer
import com.example.easySale.fragments.BarChart

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