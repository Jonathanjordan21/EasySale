package com.example.vendual.fragments

import android.Manifest
import android.app.Dialog
import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.example.vendual.*
import com.example.vendual.activities.MainActivity
import com.example.vendual.adapters.SliderBarChartAdapter
import com.example.vendual.adapters.SliderRevenueAdapter
import com.example.vendual.adapters.SliderStatsChartAdapter
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.tabs.TabLayout
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import viewpager2_transformers.Pager2_DepthTransformer
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.abs

class Statistic : Fragment(R.layout.fragment_statistic) {

    private lateinit var vp2StatsBarChart : ViewPager2
    private lateinit var vp2Stats : ViewPager2
    private lateinit var dotsIndicator: SpringDotsIndicator
    private lateinit var tabLayout : TabLayout
    private lateinit var toolbarStats : Toolbar
    private lateinit var appbarStats : AppBarLayout

    private lateinit var linearLayoutFilter : LinearLayout
    private lateinit var svFilter : ScrollView
    private lateinit var etPriceStart : EditText
    private lateinit var etPriceEnd: EditText
    private lateinit var tvDateStart : TextView
    private lateinit var tvDateEnd : TextView
    private lateinit var hideLabel : CheckBox
    private lateinit var hideProduct : CheckBox
    private lateinit var cbMerge : CheckBox
    private lateinit var labelCheckBoxes: TextView
    private lateinit var btnApplyFilter: Button

    private lateinit var btnGeneratePdf: Button

    private lateinit var dataList : ArrayList<Buyer>
    private lateinit var tempDataList : ArrayList<Buyer>


    private fun initFilterLayout(){
        svFilter = requireView().findViewById(R.id.svFilter)
        linearLayoutFilter = requireView().findViewById(R.id.LinearLayoutFilter)
        etPriceStart = requireView().findViewById(R.id.etPriceStart)
        etPriceEnd = requireView().findViewById(R.id.etPriceEnd)
        tvDateStart = requireView().findViewById(R.id.tvPickDateStart)
        tvDateEnd = requireView().findViewById(R.id.tvPickDateEnd)
        hideLabel = requireView().findViewById(R.id.cbHideLabel)
        cbMerge = requireView().findViewById(R.id.cbMerge)
        hideProduct = requireView().findViewById(R.id.cbHideProduct)
        labelCheckBoxes = requireView().findViewById(R.id.tvStatsLabel)
        btnApplyFilter = requireView().findViewById(R.id.btnApplyFilter)
        labelCheckBoxes.setOnClickShowLabelsCheckBox(requireContext())
        tvDateStart.setDatePickerOnClick(requireContext())
        tvDateEnd.setDatePickerOnClick(requireContext())
    }

    private fun filterLogic(position : Int){
        val priceTop = etPriceStart.text.toString()
        val priceBottom = etPriceEnd.text.toString()
        val dateStart = tvDateStart.text.toString()
        val dateEnd = tvDateEnd.text.toString()

        tempDataList = dataList.filter{
            val hashmap :HashMap<String,Float> = hashMapOf()

            val cond1 = if(priceBottom.isNotBlank() && priceTop.isNotBlank()){
                if(vp2StatsBarChart.currentItem == 1){

                    dataList.forEach {
                        if(!hashmap.containsKey(it.itemName)){
                            hashmap[it.itemName] = it.costs * it.quantity
                        } else hashmap[it.itemName] =
                            hashmap[it.itemName]!!.toFloat() + (it.costs * it.quantity)
                    }

                    val map2 = hashmap.filter{
                            (_,v) -> v >= priceBottom.toFloat() && v <= priceTop.toFloat()
                    }

                    map2.containsKey(it.itemName)


                }
                else {
                    dataList.forEach {
                        if(!hashmap.containsKey(it.name)){
                            hashmap[it.name] = it.costs * it.quantity
                        } else hashmap[it.name] =
                            hashmap[it.name]!!.toFloat() + (it.costs * it.quantity)
                    }

                    val map2 = hashmap.filter{
                            (_,v) -> v >= priceBottom.toFloat() && v <= priceTop.toFloat()
                    }

                    map2.containsKey(it.name)
                }
            } else true

            val cond2 = if(dateStart.isNotBlank() && dateEnd.isNotBlank()) {
                it.date >= Date(dateStart) && it.date <= Date(dateEnd)
            } else true

            val cond3 = if(hideLabel.isChecked){
                if(!it.labels.isNullOrEmpty() && it.labels!!.size == 1){
                    !it.labels!![0].isDeleted
                } else true
            } else true

            val cond4 = if(hideProduct.isChecked){ !it.isItemDeleted } else true

            val cond5 = if(labelCheckBoxes.text.isNotBlank()){
                if(!it.labels.isNullOrEmpty()) {
                    val selectedLabels = labelCheckBoxes.text.toString().trim()
                        .split(", ")
                    it.labels!!.map { it.name }.containsAll(selectedLabels)
                } else false
            } else true

            cond1 && cond2 && cond3 && cond4 && cond5
        } as ArrayList<Buyer>

        reloadAdapter(position)
    }

    private fun buttonPdfLogic(reportType : ReportType){
        val reportPdf = ReportPdf(reportType)
        if(TinyDB(requireContext()).getAutoListObject<Buyer>().isNullOrEmpty()){
            makeToast(requireContext(), "The Data is Empty!")
            return
        }
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            PackageManager.PERMISSION_GRANTED
        )

        parentFragmentManager.beginTransaction().apply{
            replace(R.id.fragmentContainer, reportPdf).commit()
        }

        Dialog(requireContext()).run{
            setContentView(R.layout.popup_report_pdf)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()

            val btnCancel = findViewById<Button>(R.id.btnCancelDownload)
            val btnDownload = findViewById<Button>(R.id.btnDownloadPdfReport)
            parentFragmentManager.beginTransaction().apply {
                btnCancel.setOnClickListener {
                    this.replace(R.id.fragmentContainer, Statistic()).commit()
                    dismiss()
                }

                btnDownload.setOnClickListener {
                    val bitmap = PDFConverter().createBitmap(reportPdf.requireContext(),
                        reportPdf.requireView(), reportPdf.requireActivity()
                    )
                    val d = Date()
                    val fileName =
                        "(${d.date}_${d.month + 1}_${d.year + 1900})_${reportType.name}_REPORT"

                    PDFConverter().convertBitmapToPdf(
                        bitmap,
                        reportPdf.requireContext(),
                        fileName
                    )
                    this.replace(R.id.fragmentContainer, Statistic()).commit()
                    dismiss()

                }
            }

        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFilterLayout()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        vp2StatsBarChart = requireView().findViewById(R.id.vp2StatsBarChart)
        vp2Stats = requireView().findViewById(R.id.vp2Stats)
        dotsIndicator = requireView().findViewById(R.id.dots_indicator_stats)
        tabLayout = requireView().findViewById(R.id.tabStats)

        toolbarStats = requireView().findViewById(R.id.toolbarStats)
        appbarStats = requireView().findViewById(R.id.appbarStats)

        btnGeneratePdf = requireView().findViewById(R.id.btnGeneratePdfReport)



        dataList = TinyDB(context).getAutoListObject()
        tempDataList = arrayListOf()
        tempDataList.addAll(dataList)



        val reportTypes = resources.getStringArray(R.array.report_type)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.report_dropdown, reportTypes)
        val acReport = requireView().findViewById<AutoCompleteTextView>(R.id.acReport)


        acReport.setText(resources.getString(R.string.daily_report))

        acReport.setAdapter(arrayAdapter)

        if(TinyDB(requireContext()).getAutoListObject<Buyer>().isNullOrEmpty()){
            btnGeneratePdf.visibility = Button.GONE
        } else btnGeneratePdf.visibility = Button.VISIBLE

        var reportType = ReportType.DAILY
        val generateLocale = resources.getString(R.string.generate_pdf)
        btnGeneratePdf.text = "$generateLocale : ${reportTypes[0]}"

        acReport.setOnItemClickListener { _, _, position, _ ->
            when(position){
                0 -> {
                    reportType = ReportType.DAILY
                    btnGeneratePdf.text = "$generateLocale : ${reportTypes[0]}"
                }
                1 -> {
                    reportType = ReportType.MONTHLY
                    btnGeneratePdf.text = "$generateLocale : ${reportTypes[1]}"
                }
                2 -> {
                    reportType = ReportType.ANNUAL
                    btnGeneratePdf.text = "$generateLocale : ${reportTypes[2]}"
                }
            }
            //val reportPdf = ReportPdf(reportType)
            btnGeneratePdf.setOnClickListener { buttonPdfLogic(reportType) }

            vp2Stats.adapter = SliderStatsChartAdapter(requireActivity(), reportType)
            dotsIndicator.setViewPager2(vp2Stats)
            vp2Stats.setPageTransformer(Pager2_DepthTransformer())
        }

        btnGeneratePdf.setOnClickListener { buttonPdfLogic(reportType) }



        vp2StatsBarChart.adapter = SliderBarChartAdapter(requireActivity(), tempDataList)
        vp2Stats.adapter = SliderStatsChartAdapter(requireActivity(), reportType)

        dotsIndicator.setViewPager2(vp2Stats)
        vp2StatsBarChart.setPageTransformer(Pager2_DepthTransformer())
        vp2Stats.setPageTransformer(Pager2_DepthTransformer())









        btnApplyFilter.setOnClickListener {
            val priceTop = etPriceStart.text.toString()
            val priceBottom = etPriceEnd.text.toString()
            val dateStart = tvDateStart.text.toString()
            val dateEnd = tvDateEnd.text.toString()

            if(priceBottom.isNotBlank() && priceTop.isNotBlank() &&
                priceBottom.toFloat() > priceTop.toFloat()){
                etPriceStart.setText("")
                etPriceEnd.setText("")
                Toast.makeText(context,
                    "Start Price cannot be Higher than End Price",
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(dateStart.isNotBlank() && dateEnd.isNotBlank() &&
                Date(dateStart) > Date(dateEnd)){
                tvDateStart.text = ""
                tvDateEnd.text = ""
                Toast.makeText(context,
                    "Start Day cannot be later than End Day",
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            filterLogic(vp2StatsBarChart.currentItem)
            svFilter.visibility = ScrollView.GONE
            //linearLayoutFilter.visibility = LinearLayout.GONE
        }

        appbarStats
            .addOnOffsetChangedListener(
                AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                    if (abs(verticalOffset) == appBarLayout.totalScrollRange) {
                        // Collapsed

                        Log.d("HELLO", "THIS IS COLLAPSED")

                        toolbarStats.visibility = Toolbar.VISIBLE

                        toolbarStats.run{
                            setBackgroundColor(Color.WHITE)
                            val searchItem: MenuItem = menu.findItem(R.id.search_bar)
                            val filterItem: MenuItem = menu.findItem(R.id.filterMenu)

                            filterItem.setOnMenuItemClickListener {
                                if(svFilter.visibility != ScrollView.GONE){
                                    svFilter.visibility = ScrollView.GONE
                                } else {
                                    svFilter.visibility = ScrollView.VISIBLE
                                    svFilter.background.setTint(resources.getColor(R.color.green))
                                }
                                true
                            }

                            searchItem.icon.setTint(Color.BLUE)
                            //val searchManager = getSystemService(requireContext(), SearchManager::class.java) as SearchManager
                            val searchView: SearchView = searchItem.actionView as SearchView
                            searchView.queryHint = "Search Here"
                            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                                override fun onQueryTextSubmit(query: String?): Boolean {
                                    val adapterPosition = vp2StatsBarChart.currentItem
                                    Log.d("ADAPTER POSITION", "$adapterPosition")
                                    if(!query.isNullOrBlank()){
                                        val queryLc = query.lowercase()
                                        tempDataList.clear()

                                        tempDataList = if(adapterPosition == 0){
                                            dataList.filter{
                                                it.name.lowercase().contains(queryLc)
                                            } as ArrayList<Buyer>
                                        } else {
                                            dataList.filter{
                                                it.itemName.lowercase().contains(queryLc)
                                            } as ArrayList<Buyer>
                                        }
                                        reloadAdapter(adapterPosition)
                                    } else {
                                        tempDataList.clear()

                                        tempDataList.addAll(dataList)
                                        reloadAdapter(adapterPosition)
                                    }
                                    return true
                                }

                                override fun onQueryTextChange(query: String?): Boolean {
                                    val adapterPosition = vp2StatsBarChart.currentItem
                                    if(query.isNullOrBlank()){
                                        tempDataList.clear()
                                        tempDataList.addAll(dataList)
                                        reloadAdapter(adapterPosition)
                                    }
                                    return true
                                }
                            })
                        }


                    } else if (verticalOffset == 0) {
                        // Expanded


                        Log.d("HELLO", "THIS IS EXPANDED")

                        toolbarStats.visibility = Toolbar.GONE


                    } else {
                        // Somewhere in between
                        Log.d("HELLO", "THIS IS SOMEWHERE IN BETWEEN")
                        svFilter.visibility = ScrollView.GONE
                        toolbarStats.visibility = Toolbar.INVISIBLE
                    }
        })



        tabLayout.addTab(tabLayout.newTab().setText(resources.getString(R.string.buyers)))
        tabLayout.addTab(tabLayout.newTab().setText(resources.getString(R.string.products)))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                vp2StatsBarChart.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        vp2StatsBarChart.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })
    }


    private fun reloadAdapter(currPosition: Int = 0){
        var merge = false
        if(cbMerge.isChecked){
            merge = true
        }
        vp2StatsBarChart.adapter = SliderBarChartAdapter(requireActivity(), tempDataList, merge)
        vp2StatsBarChart.currentItem = currPosition
    }


//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.search_filter_toolbar, menu)
//
//        val searchItem: MenuItem = menu.findItem(R.id.search_bar)
//        searchItem.icon.setTint(Color.BLUE)
//        val searchManager = getSystemService(requireContext(), SearchManager::class.java) as SearchManager
//        val searchView: SearchView = searchItem.actionView as SearchView
//
//        //searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity))
//        return super.onCreateOptionsMenu(menu,inflater)
//    }
}