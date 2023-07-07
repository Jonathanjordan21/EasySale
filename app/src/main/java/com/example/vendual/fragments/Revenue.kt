package com.example.vendual.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.vendual.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Revenue(daysInterval : Int, titleText : String) : Fragment(R.layout.fragment_revenue) {
    private lateinit var tvTitle : TextView
    private lateinit var tvSymbol : TextView
    private lateinit var tvPrice : TextView
    private lateinit var tinyDB: TinyDB
    private lateinit var buyerList: ArrayList<Buyer>
    private val title = titleText
    private val interval = daysInterval


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tinyDB = TinyDB(context)
        buyerList = tinyDB.getAutoListObject()
        tvTitle = requireView().findViewById(R.id.tvRevTitle)
        tvSymbol = requireView().findViewById(R.id.tvRevSymbol)
        tvPrice = requireView().findViewById(R.id.tvRevPrice)

        tvTitle.text = title

        tvPrice.text = interval.toString()

    }

}