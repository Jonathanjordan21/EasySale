package com.example.easySale.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.easySale.*
import com.example.easySale.fragments.*

class SliderRevenueAdapter(private val activity : FragmentActivity,
                           private val buyerList : ArrayList<Buyer> = TinyDB(activity).getAutoListObject())
    : FragmentStateAdapter(activity) {



    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment = when(position){

//        0 -> Revenue(buyerList.calculateCostsByDays(0,1), "Daily Revenue")
//        1 -> Revenue(buyerList.calculateCostsByDays(0,30), "Monthly Revenue")
//        2 -> Revenue(buyerList.calculateCostsByDays(0,365), "Annually Revenue")
//        else -> Revenue(buyerList.calculateCostsByDays(0,30), "Monthly Revenue")

        0 -> Revenue(buyerList.calculateCostsByDays(0,1), activity.resources.getString(R.string.daily_revenue))
        1 -> Revenue(buyerList.calculateCostsByDays(0,30), activity.resources.getString(R.string.monthly_revenue))
        2 -> Revenue(buyerList.calculateCostsByDays(0,365), activity.resources.getString(R.string.annual_revenue))
        else -> Revenue(buyerList.calculateCostsByDays(0,30), activity.resources.getString(R.string.monthly_revenue))
    }

}