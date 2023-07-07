package com.example.easySale.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.easySale.*
import com.example.easySale.ChartType.*
import com.example.easySale.fragments.*


class SliderStatsChartAdapter(fragment : FragmentActivity,
                              private val reportType : ReportType = ReportType.ANNUAL)
    : FragmentStateAdapter(fragment) {


    override fun getItemCount(): Int = when(reportType){
        ReportType.DAILY -> 4
        ReportType.MONTHLY -> 5
        ReportType.ANNUAL -> 6
    }

//    override fun createFragment(position: Int): Fragment = when(position){
//        0 -> MpBarChart()
//        1 -> LineChart()
//        else -> PieChart()
//    }

    override fun createFragment(position: Int): Fragment = when(reportType) {
        ReportType.MONTHLY -> when (position) {
            0 -> AnyChart(DEFAULT_INCOME, ReportType.MONTHLY)
            1 -> AnyChart(PRODUCT_INCOME, ReportType.MONTHLY)
            2 -> AnyChart(PRODUCT_QUANTITY, ReportType.MONTHLY)
            3 -> AnyChart(LABEL_INCOME, ReportType.MONTHLY)
            4 -> AnyChart(LABEL_QUANTITY, ReportType.MONTHLY)
            else -> AnyChart(DEFAULT_INCOME, ReportType.MONTHLY)
        }

        ReportType.DAILY -> when(position){
            0 -> AnyChart(PRODUCT_INCOME, ReportType.DAILY)
            1 -> AnyChart(PRODUCT_QUANTITY, ReportType.DAILY)
            2 -> AnyChart(LABEL_INCOME, ReportType.DAILY)
            3 -> AnyChart(LABEL_QUANTITY, ReportType.DAILY)
            else -> AnyChart(PRODUCT_INCOME, ReportType.DAILY)
        }

        ReportType.ANNUAL -> when (position) {
            0 -> AnyChart(DEFAULT_INCOME, ReportType.ANNUAL)
            1 -> AnyChart(PRODUCT_INCOME, ReportType.ANNUAL)
            2 -> AnyChart(PRODUCT_QUANTITY, ReportType.ANNUAL)
            3 -> AnyChart(LABEL_INCOME, ReportType.ANNUAL)
            4 -> AnyChart(LABEL_QUANTITY, ReportType.ANNUAL)
            5 -> AnyChart(ANNUAL_INCOME, ReportType.ANNUAL)
            else -> AnyChart(DEFAULT_INCOME, ReportType.ANNUAL)
        }
    }


}