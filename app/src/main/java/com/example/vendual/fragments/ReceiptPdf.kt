package com.example.vendual.fragments

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vendual.R
import com.example.vendual.Summary
import com.example.vendual.adapters.PurchaseAdapter
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class ReceiptPdf(private val sumList : ArrayList<Summary>, private val total: CharSequence)
    : Fragment(R.layout.fragment_receipt_pdf) {
    private lateinit var rcv : RecyclerView
    private lateinit var date : TextView
    private lateinit var tvTotal : TextView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rcv = requireView().findViewById(R.id.rcvPurchase)
        date = requireView().findViewById(R.id.tvDate)
        tvTotal = requireView().findViewById(R.id.tvTotal)


        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        date.text = resources.getString(R.string.date) + " " + formatter.format(Date()).toString()

        rcv.adapter = PurchaseAdapter(requireContext(), sumList)

        rcv.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)



        tvTotal.text = "\t".repeat(34) +" "+ total
    }



}