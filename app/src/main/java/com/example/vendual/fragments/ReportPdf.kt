package com.example.vendual.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.example.vendual.*

class ReportPdf(private val reportType: ReportType) : Fragment(R.layout.report_pdf) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val reportType = ReportType.MONTHLY

        val tinyDB = TinyDB(activity)
        val dataItemList = tinyDB.getAutoListObject<Items>()
        val buyerList =  tinyDB.getAutoListObject<Buyer>()
            .filterBuyerList(reportType) as ArrayList<Buyer>
        val labelList = tinyDB.getAutoListObject<Labels>()

        val title = view.findViewById<TextView>(R.id.pdfReportTitle)


        requireActivity().supportFragmentManager.beginTransaction().apply{
            replace(R.id.pdfChart2, AnyChart(ChartType.PRODUCT_INCOME, reportType, true))
            replace(R.id.pdfChart3, AnyChart(ChartType.DEFAULT_INCOME, reportType, true))
        }.commit()


        val totalIncome = view.findViewById<TextView>(R.id.tvTotalIncome)
        val medianIncome = view.findViewById<TextView>(R.id.tvMedianIncome)
        val averageIncome = view.findViewById<TextView>(R.id.tvAverageIncome)
        val totalSoldOut = view.findViewById<TextView>(R.id.tvSumSoldOut)
        val mostLabelIncome = view.findViewById<TextView>(R.id.tvLabelIncome)
        val favLabel = view.findViewById<TextView>(R.id.tvFavLabel)

        when(reportType){
            ReportType.ANNUAL -> title.text = resources.getString(R.string.annual_report)
            ReportType.DAILY -> title.text = resources.getString(R.string.daily_report)
            ReportType.MONTHLY -> title.text = resources.getString(R.string.monthly_report)
        }

        val priceList = buyerList.map{it.quantity*it.costs}.sortedBy { it }
        totalIncome.text = priceList.sum().toString().replace('.', ',')
        averageIncome.text = (priceList.sum() / dataItemList.size).toString().replace('.', ',')
        val pos = if(priceList.size%2==0){
            Pair((priceList.size/2), (priceList.size/2 + 1))
        } else Pair((priceList.size+1)/2, null)

        val median = if(pos.second == null) priceList[pos.first-1]
        else (priceList[pos.first-1] + priceList[pos.second!!-1])/2

        medianIncome.text = median.toString().replace('.', ',')

        totalSoldOut.text = buyerList.map{it.quantity}.sum().toString()

        val qMapList = buyerList.labelQuantityHashMap(labelList)
        val iMapList = buyerList.labelIncomeHashMap(labelList)


        val i = buyerList.labelIncomeHashMap(labelList).maxOf { (_,v)->v }
        val q = buyerList.labelQuantityHashMap(labelList).maxOf{(_,v)->v}
        var iName = ""
        var qName = ""
        qMapList.forEach {(k,v) -> if(v == q) qName = k.name}
        iMapList.forEach {(k,v) -> if(v== i) iName = k.name}

        val soldStr = resources.getString(R.string.sold)
        val revStr = resources.getString(R.string.revenue)
        favLabel.text = "$qName ($soldStr $q)"
        mostLabelIncome.text = "$iName ($revStr Rp $i)"
    }

}