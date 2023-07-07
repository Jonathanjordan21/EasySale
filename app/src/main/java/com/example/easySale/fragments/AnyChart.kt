package com.example.easySale.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Cartesian
import com.anychart.core.cartesian.series.Column
import com.anychart.core.cartesian.series.Line
import com.anychart.data.Mapping
import com.anychart.data.Set
import com.anychart.enums.*
import com.anychart.graphics.vector.Stroke
import com.example.easySale.*
import com.example.easySale.ChartType.*
import com.anychart.enums.HoverMode

import com.anychart.enums.TooltipPositionMode
import com.anychart.core.cartesian.series.Bar
import com.anychart.core.cartesian.series.JumpLine
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import com.anychart.enums.TooltipDisplayMode





class AnyChart(private val chartType : ChartType,
               private val reportType : ReportType, private val pdf : Boolean = false)
    : Fragment(R.layout.fragment_any_chart) {

    private lateinit var anyChart : AnyChartView
    private lateinit var creditHider : TextView
    private lateinit var data : ArrayList<DataEntry>

    private lateinit var dataList : ArrayList<Buyer>
    private lateinit var buyerList : List<Buyer>
    private lateinit var itemsList : ArrayList<Items>
    private lateinit var labelList : ArrayList<Labels>

    private lateinit var tinyDB :TinyDB

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        anyChart = requireView().findViewById(R.id.anyChart)
        creditHider = requireView().findViewById(R.id.tvCreditHider)
        tinyDB = TinyDB(context)
        data = arrayListOf()

        anyChart.setProgressBar(requireView().findViewById(R.id.progressBar))

        dataList = tinyDB.getAutoListObject()
        itemsList= tinyDB.getAutoListObject()
        labelList = tinyDB.getAutoListObject()
        buyerList = filterBuyerList()
        Log.d("DATE == DATE", "${dataList.filter { it.date == Date() }}")
        dataList.forEach{
            Log.d("DATE", "${it.date} == ${Date()}")
        }

        when(chartType){
            PRODUCT_QUANTITY, LABEL_QUANTITY -> setUpVertChart()
            PRODUCT_INCOME, LABEL_INCOME -> setUpColumnChart()
            DEFAULT_INCOME, ANNUAL_INCOME -> setupLineChart2()
        }

        anyChart.setZoomEnabled(true)
    }


    private fun filterBuyerList() : List<Buyer> = when(reportType){
        ReportType.DAILY -> dataList.filter { it.date.date == Date().date &&
                it.date.year == Date().year && it.date.month == Date().month
        }
        ReportType.MONTHLY -> dataList.filter {
            it.date.year == Date().year && it.date.month == Date().month
        }
        ReportType.ANNUAL -> if(chartType == ANNUAL_INCOME) dataList
        else dataList.filter { it.date.year == Date().year }
    }


    private fun labelIncomeHashMap() : HashMap<Labels, Float>{

        val hashmap = hashMapOf<Labels, Float>()

        labelList.forEach { hashmap[it] = 0f}
        val labelNull = Labels("NoCategory", Color.RED, "tag.png")
        hashmap[labelNull] = 0f

        buyerList.forEach{ itBuyer ->
            itBuyer.labels?.forEach {
                hashmap[it] = hashmap[it]!! + (itBuyer.costs * itBuyer.quantity)
            }
            if(itBuyer.labels.isNullOrEmpty()){
                hashmap[labelNull] =
                    hashmap[labelNull]!! + (itBuyer.costs * itBuyer.quantity)
            }
        }
        return hashmap
    }

    private fun labelQuantityHashMap(): HashMap<Labels, Int>{

        val hashmap = hashMapOf<Labels, Int>()

        labelList.forEach { hashmap[it] = 0}
        val labelNull = Labels("NoCategory", Color.RED, "tag.png")
        hashmap[labelNull] = 0

        buyerList.forEach{ itBuyer ->
            itBuyer.labels?.forEach {
                hashmap[it] = hashmap[it]!! + itBuyer.quantity
            }
            if(itBuyer.labels.isNullOrEmpty()){
                hashmap[labelNull] =
                    hashmap[labelNull]!! + itBuyer.quantity
            }
        }
        return hashmap
    }


    private fun productIncomeHashMap() : HashMap<String, Float>{
        val hashmap = hashMapOf<String, Float>()

        itemsList.forEach { hashmap[it.title] = 0f}

        buyerList.forEach{
            if(hashmap.containsKey(it.itemName)){
                hashmap[it.itemName] = hashmap[it.itemName]!! + it.quantity * it.costs
            }
        }
        return hashmap
    }

    private fun productQuantityHashMap() : HashMap<String, Int>{
        val hashmap = hashMapOf<String, Int>()

        itemsList.forEach { hashmap[it.title] = 0}

        buyerList.forEach{
            if(hashmap.containsKey(it.itemName)){
                hashmap[it.itemName] = hashmap[it.itemName]!! + it.quantity
            }
        }
        return hashmap
    }


    private fun dateIncomeHashMap(): HashMap<Int, Float?> {
        val hashmap: HashMap<Int, Float?> = hashMapOf()
        val bmap : Map<Date, Float> = buyerList.groupingBy { it.date }.fold(0f)
        { init, v -> init + (v.quantity * v.costs) }


        if(reportType == ReportType.ANNUAL) {
            for(x in 1..12){
                if(x <= Date().month + 1) hashmap[x] = 0f
                else hashmap[x] = null
            }

            bmap.forEach{(k,v)->

                if(Date().year == k.year) {
                    if (hashmap.containsKey(k.month + 1))
                        hashmap[k.month + 1] = hashmap[k.month + 1]!! + v
                }
            }

        } else if(reportType == ReportType.MONTHLY){

            for(x in 1..Date().getDaysOfMonth()){
                if(x <= Date().date) {
                    hashmap[x] = 0f
                } else hashmap[x] = null
            }

            bmap.forEach{(k,v)->
                if(Date().year == k.year && k.month == Date().month) {
                    if (hashmap.containsKey(k.date))
                        hashmap[k.date] = hashmap[k.date]!! + v
                }
            }

        }


        return hashmap
    }



    private fun setUpVertChart(){

        val vertical: Cartesian = AnyChart.vertical()

        vertical.background().fill("black")

        vertical.yAxis(0).title("Quantity")

        if(chartType == PRODUCT_QUANTITY) {
            vertical.animation(true)
                .title("Number of Items")
            vertical.xAxis(0).title("Items")
            val list = productQuantityHashMap().toList().sortedBy { (_, value) -> value}
            for (x in list.indices){
                var name = list[x].first
                if (list[x].first.length > 10) {
                    name = list[x].first.replaceRange(10, list[x].first.length, "...")
                }
                data.add(ValueDataEntry(name, list[x].second))

                if(pdf && x>1) break
            }

        } else if(chartType == LABEL_QUANTITY){
            vertical.animation(true)
                .title("Number of Categories Sold")
            vertical.xAxis(0).title("Label/Category")
            val list = labelQuantityHashMap()
            list.forEach { (k, v) ->
                var name = k.name
                if (k.name.length > 10) {
                    name = k.name.replaceRange(10, k.name.length, "...")
                }
                data.add(ValueDataEntry(name, v))
            }
        }

        val set = Set.instantiate()
        set.data(data)
        val barData = set.mapAs("{ x: 'x', value: 'value' }")
        val jumpLineData = set.mapAs("{ x: 'x', value: 'jumpLine' }")

        val bar: Bar = vertical.bar(barData)
        bar.labels().format("{%Value}")

        val jumpLine: JumpLine = vertical.jumpLine(jumpLineData)
        jumpLine.stroke("2 #60727B")
        jumpLine.labels().enabled(false)

        vertical.yScale().minimum(0.0)

        vertical.labels(true)

        vertical.tooltip()
            .displayMode(TooltipDisplayMode.UNION)
            .positionMode(TooltipPositionMode.POINT)
            .unionFormat(
                """function() {
      return 'Quantity: ' + this.points[1].value + ' ';
    }"""
            )

        vertical.interactivity().hoverMode(HoverMode.BY_X)

        vertical.xAxis(true)
        vertical.yAxis(true)
        vertical.yAxis(0).labels().format("{%Value}")
        anyChart.setChart(vertical);
    }


    private fun setUpColumnChart(){
        data.clear()

        val cartesian: Cartesian = AnyChart.column()

        cartesian.background().fill("black");


        if(chartType == LABEL_INCOME) {
            cartesian.xAxis(0).title("Label/Category")
            cartesian.yAxis(0).title("Money")
            cartesian.title("Funds of Labels")
            val hashmap = labelIncomeHashMap()
            hashmap.forEach{(k,v)->
                var name = k.name
                if(name.length > 10) {
                    name = k.name.replaceRange(10, name.length, "...")
                }
                data.add(ValueDataEntry(name, v))
            }
        } else if(chartType == PRODUCT_INCOME){
            cartesian.xAxis(0).title("Items")
            cartesian.yAxis(0).title("Money")
            cartesian.title("Funds of Items")
            val list = productIncomeHashMap().toList().sortedBy { (_, value) -> value}
            for (x in list.indices){
                var name = list[x].first
                if (list[x].first.length > 10) {
                    name = list[x].first.replaceRange(10, list[x].first.length, "...")
                }
                data.add(ValueDataEntry(name, list[x].second))

                if(pdf && x>4) break

            }

//            list.forEach{(k,v)->
//                var name = k
//                if(k.length > 10) {
//                    name = k.replaceRange(10, name.length, "...")
//                }
//                data.add(ValueDataEntry(name, v))
//            }
        }



        val column: Column = cartesian.column(data)

        column.tooltip()
            .titleFormat("{%X}")
            .position(Position.CENTER_BOTTOM)
            .anchor(Anchor.CENTER_BOTTOM)
            .offsetX(0.0)
            .offsetY(5.0)
            .format("Funds : {%Value}{groupsSeparator: }")

        cartesian.animation(true)


        cartesian.yScale().minimum(0.0)

        cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }")

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT)
        cartesian.interactivity().hoverMode(HoverMode.BY_X)



        anyChart.setChart(cartesian)
    }


    private fun setupLineChart2(){
        val cartesian: Cartesian = AnyChart.line()

        cartesian.animation(true)
        cartesian.background().fill("black")

        cartesian.padding(10.0, 20.0, 5.0, 20.0)

        cartesian.xScale("continuous")

        cartesian.crosshair().enabled(true)
        cartesian.crosshair()
            .yLabel(true) // TODO yStroke
            .yStroke(null as Stroke?, null, null, null as String?, null as String?)

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT)
        cartesian.yAxis(0).title("Money")

        if(reportType == ReportType.MONTHLY) {
            cartesian.title("Daily Cash Flow per Month")
            cartesian.xAxis(0).labels().padding(5.0, 5.0, 5.0, 5.0)
            cartesian.xAxis(0).title("Day Of Month")

        } else if(reportType == ReportType.ANNUAL){
            cartesian.title("Monthly Cash Flow per Year")
            cartesian.xAxis(0).labels().padding(5.0, 5.0, 5.0, 5.0)
            cartesian.xAxis(0).title("Month")
        }

        if(chartType == ANNUAL_INCOME){
            cartesian.title("Annual Cash Flow")
            cartesian.xAxis(0).labels().padding(5.0, 5.0, 5.0, 5.0)
            cartesian.xAxis(0).title("Year")
        }

//        val dataList = tinyDB.getAutoListObject<Buyer>()
//
//        val groupedData = dataList.groupingBy { it.date }.fold(0f){init, v ->
//            init + (v.quantity * v.costs)
//        }

//        groupedData.toSortedMap().forEach{ (k,v) ->
//            data.add(ValueDataEntry("${k.date}",v))
//        }


        val hashmap : HashMap<Int, Float?> = if(chartType == ANNUAL_INCOME) {
            buyerList.groupingBy { it.date.year+1900 }.fold(0f) { init, v ->
                init + v.costs * v.quantity
            } as HashMap<Int, Float?>
        } else dateIncomeHashMap()



//        for(x in 1..Date().getDaysOfMonth()){
//            if(x < Date().date) {
//                hashmap[x] = 0f
//            } else hashmap[x] = null
//        }
//
//        groupedData.forEach{ (k,v) ->
//            if(Date().month == k.month && Date().year == k.year) {
//                if (hashmap.containsKey(k.date)) hashmap[k.date] = v
//            }
//        }


        hashmap.forEach{ (k,v) -> data.add(ValueDataEntry("$k",v)) }


        val set = Set.instantiate()
        set.data(data)
        val series1Mapping = set.mapAs("{ x: 'x', value: 'value' }")
//        val series2Mapping = set.mapAs("{ x: 'x', value: 'value2' }")
//        val series3Mapping = set.mapAs("{ x: 'x', value: 'value3' }")

        val series1 = cartesian.line(series1Mapping)
        series1.name("Money")
        series1.hovered().markers().enabled(true)
        series1.hovered().markers()
            .type(MarkerType.CIRCLE)
            .size(4.0)
        series1.tooltip()
            .position("right")
            .anchor(Anchor.LEFT_CENTER)
            .offsetX(5.0)
            .offsetY(5.0)
        series1.hovered().stroke("#0066cc, 2")

//        val series2 = cartesian.line(series2Mapping)
//        series2.name("Whiskey")
//        series2.hovered().markers().enabled(true)
//        series2.hovered().markers()
//            .type(MarkerType.CIRCLE)
//            .size(4.0)
//        series2.tooltip()
//            .position("right")
//            .anchor(Anchor.LEFT_CENTER)
//            .offsetX(5.0)
//            .offsetY(5.0)
//
//        val series3 = cartesian.line(series3Mapping)
//        series3.name("Tequila")
//        series3.hovered().markers().enabled(true)
//        series3.hovered().markers()
//            .type(MarkerType.CIRCLE)
//            .size(4.0)
//        series3.tooltip()
//            .position("right")
//            .anchor(Anchor.LEFT_CENTER)
//            .offsetX(5.0)
//            .offsetY(5.0)

        cartesian.legend().enabled(true)
        cartesian.legend().fontSize(13.0)
        cartesian.legend().padding(0.0, 0.0, 10.0, 0.0)

        anyChart.setChart(cartesian)

    }



    private fun setupLineChart(){
        data.clear()

        val cartesian = AnyChart.line();

        cartesian.animation(true);

        cartesian.padding(10f, 20f, 5f, 20f);

        cartesian.crosshair().enabled(true);
        val stroke : Stroke? = null
        val str1: String? = null
        val str2 : String? = null
        cartesian.crosshair()
            .yLabel(true)
            // TODO yStroke
            .yStroke(stroke, null, null, str1, str2);

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

        cartesian.title("Cash Flow of Dates");

        cartesian.yAxis(0).title("Currency");
        cartesian.xAxis(0).labels().padding(5f, 5f, 5f, 5f);

        val dataList = tinyDB.getAutoListObject<Buyer>()

        val groupedData = dataList.groupingBy { it.date }.fold(0f){init, v ->
            init + (v.quantity * v.costs)
        }

        groupedData.toSortedMap().forEach{ (k,v) ->
            data.add(ValueDataEntry("${k.day}",v))
        }


//        data.add(CustomDataEntry("1986", 3.6, 2.3, 2.8));
//        data.add(CustomDataEntry("1987", 7.1, 4.0, 4.1));
//        data.add(CustomDataEntry("1988", 8.5, 6.2, 5.1));
//        data.add(CustomDataEntry("1989", 9.2, 11.8, 6.5));
//        data.add(CustomDataEntry("1990", 10.1, 13.0, 12.5));
//        data.add(CustomDataEntry("1991", 11.6, 13.9, 18.0));
//        data.add(CustomDataEntry("1992", 16.4, 18.0, 21.0));
//        data.add(CustomDataEntry("1993", 18.0, 23.3, 20.3));
//        data.add(CustomDataEntry("1994", 13.2, 24.7, 19.2));
//        data.add(CustomDataEntry("1995", 12.0, 18.0, 14.4));
//        data.add(CustomDataEntry("1996", 3.2, 15.1, 9.2));
//        data.add(CustomDataEntry("1997", 4.1, 11.3, 5.9));
//        data.add(CustomDataEntry("1998", 6.3, 14.2, 5.2));
//        data.add(CustomDataEntry("1999", 9.4, 13.7, 4.7));
//        data.add(CustomDataEntry("2000", 11.5, 9.9, 4.2));
//        data.add(CustomDataEntry("2001", 13.5, 12.1, 1.2));
//        data.add(CustomDataEntry("2002", 14.8, 13.5, 5.4));
//        data.add(CustomDataEntry("2003", 16.6, 15.1, 6.3));
//        data.add(CustomDataEntry("2004", 18.1, 17.9, 8.9));
//        data.add(CustomDataEntry("2005", 17.0, 18.9, 10.1));
//        data.add(CustomDataEntry("2006", 16.6, 20.3, 11.5));
//        data.add(CustomDataEntry("2007", 14.1, 20.7, 12.2));
//        data.add(CustomDataEntry("2008", 15.7, 21.6, 10));
//        data.add(CustomDataEntry("2009", 12.0, 22.5, 8.9));

        val set = Set.instantiate()
        val series1Mapping : Mapping = set.mapAs("{ x: 'x', value: 'value' }")
        val series1 : Line = cartesian.line(series1Mapping);
        series1.name("Money");
        series1.hovered().markers().enabled(true);
        series1.hovered().markers()
            .type(MarkerType.CIRCLE)
            .size(4f);
        series1.tooltip()
            .position("right")
            .anchor(Anchor.LEFT_CENTER)
            .offsetX(5f)
            .offsetY(5f);


        cartesian.legend().enabled(true);
        cartesian.legend().fontSize(13f);
        cartesian.legend().padding(0f, 0f, 10f, 0f);

        anyChart.setChart(cartesian);
    }

//    private class CustomDataEntry(x: String, value: Number,
//                                  value2 : Number, value3 : Number)
//        : ValueDataEntry(x, value) {
//        init{
//            setValue("value2", value2)
//            setValue("value3", value3)
//        }
////        CustomDataEntry(String x, Number value, Number value2, Number value3) {
////            super(x, value);
////            setValue("value2", value2);
////            setValue("value3", value3);
////        }
//
//    }


}