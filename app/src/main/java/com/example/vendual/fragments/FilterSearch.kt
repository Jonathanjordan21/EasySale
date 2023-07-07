package com.example.vendual.fragments

import android.content.Context
import com.example.vendual.fragments.MainMenu.*
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.vendual.*
import com.example.vendual.adapters.SliderRevenueAdapter
import com.example.vendual.adapters.VendItemAdapter
import com.google.android.material.textfield.TextInputLayout

class FilterSearch : Fragment(R.layout.fragment_filter_search) {
    private lateinit var tinyDB : TinyDB
    private lateinit var priceFrom : EditText
    private lateinit var priceEnd : EditText
    private lateinit var soldFrom : EditText
    private lateinit var soldEnd : EditText
    private lateinit var dropdownLabel : TextInputLayout
    private lateinit var acFilterLabel : AutoCompleteTextView
    private lateinit var btnSearch : Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tinyDB = TinyDB(context)

        btnSearch = requireView().findViewById(R.id.btnSearchFilterLabel)
        priceFrom = requireView().findViewById(R.id.priceFrom)
        priceEnd = requireView().findViewById(R.id.priceEnd)
        soldFrom = requireView().findViewById(R.id.soldFrom)
        soldEnd = requireView().findViewById(R.id.soldEnd)
//        dropdownLabel = requireView().findViewById(R.id.dropdownLabel)
//        acFilterLabel = requireView().findViewById(R.id.acFilterLabel)
//
//        val labelsList = tinyDB.getListObject(LABEL_DB, Labels::class.java) as MutableList<Labels>
//
//        val ddMenuItems = labelsList.map{it.name} as MutableList
//
//        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.filter_label_dropdown, ddMenuItems)
//
//        acFilterLabel.setAdapter(arrayAdapter)

        btnSearch.setOnClickListener {
            val parentRecView : RecyclerView = requireParentFragment().requireView().findViewById(R.id.rec_view)
            val parentSearchView : EditText = requireParentFragment().requireView().findViewById(R.id.etSearchBar)
            val parentViewPager2 : ViewPager2 = requireParentFragment().requireView().findViewById(R.id.vp2Revenue)
            val vendItems = tinyDB.getAutoListObject<Items>()
            val tempItems = vendItems.filter{
                val condPrice1 = if(!priceFrom.text.isNullOrBlank()) {
                    it.price >= priceFrom.text.toString().toInt()
                } else true

                val condPrice2 = if(!priceEnd.text.isNullOrBlank()) {
                    it.price <= priceEnd.text.toString().toInt()
                } else true

                val condSold1 = if(!soldFrom.text.isNullOrBlank()) {
                    it.sold >= soldFrom.text.toString().toInt()
                } else true

                val condSold2 = if(!soldEnd.text.isNullOrBlank()) {
                    it.sold <= soldEnd.text.toString().toInt()
                } else true


                condPrice1 && condPrice2 && condSold1 && condSold2

            } as MutableList<Items>

            val tempItems2 = MutableList(tempItems.size){tempItems[it]}

            val adapter = VendItemAdapter(requireContext(), tempItems2,requireActivity(), parentViewPager2)

            parentRecView.adapter = adapter

            parentSearchView.searchVendItems(tempItems2, tempItems, parentRecView)
            Log.d("Curent Filter Fragment id", "${this.id}  $this ${this.context}")
            requireParentFragment().childFragmentManager.beginTransaction().remove(this).commit()

        }

    }



}