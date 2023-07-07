package com.example.vendual.fragments

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.vendual.R
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.vendual.adapters.VendItemAdapter
import com.example.vendual.*
import com.example.vendual.activities.LoginActivity
import com.example.vendual.adapters.SliderRevenueAdapter
import java.util.*
import kotlin.reflect.KClass

import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import viewpager2_transformers.Pager2_DepthTransformer
import kotlin.collections.ArrayList


class MainMenu : Fragment(R.layout.fragment_main_menu) {
    private lateinit var rcv: RecyclerView
    private lateinit var vendItemAdapter : VendItemAdapter
    private lateinit var vendItems : MutableList<Items>
    private lateinit var tempItems : MutableList<Items>
    private lateinit var btnAddNew : Button
    private lateinit var btnFilter : Button
    private lateinit var btnGeneratePdf : Button
    private lateinit var btnReset : Button
    private lateinit var btnOcr : Button
    private lateinit var tinyDB : TinyDB
    private lateinit var etSearchBar : EditText

    private lateinit var buyerList : ArrayList<Buyer>
    private lateinit var vp2Revenue : ViewPager2

    private val filterSearch = FilterSearch()

    private lateinit var accName: TextView
    private lateinit var walletAcc : LinearLayout


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tinyDB = TinyDB(context)
        buyerList = tinyDB.getAutoListObject()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        walletAcc = requireView().findViewById(R.id.walletAcc)
        accName = requireView().findViewById(R.id.tvAccountName)

        walletAcc.setOnClickListener {
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        accName.text = tinyDB.getString(CURRENT_WALLET)


        //Initializing views
        btnOcr = requireView().findViewById(R.id.btnOCR)
        btnOcr.setOnClickListener {
            parentFragmentManager.beginTransaction().apply{
                replace(R.id.fragmentContainer, Ocr()).commit()
            }
        }
        btnOcr.visibility = Button.GONE

//        btnReset = requireView().findViewById(R.id.btnReset)
//        btnReset.setOnClickListener {
//            TinyDB(requireContext()).remove(BUYER_DB)
//            TinyDB(requireContext()).remove(LABEL_DB)
//            TinyDB(requireContext()).remove(VEND_DB)
//            vp2Revenue.adapter?.notifyDataSetChanged()
//            rcv.adapter?.notifyDataSetChanged()
//        }

        btnGeneratePdf = requireView().findViewById(R.id.btnGeneratePdfMenu)
        vp2Revenue = requireView().findViewById(R.id.vp2Revenue)
        vp2Revenue.adapter = SliderRevenueAdapter(requireActivity())
        val dotsIndicator = requireView().findViewById<WormDotsIndicator>(R.id.dots_indicator)
        dotsIndicator.setViewPager2(vp2Revenue)
        vp2Revenue.setPageTransformer(Pager2_DepthTransformer())



        var id: KClass<*>? = null

        btnFilter = requireView().findViewById(R.id.btnFilter)
        btnFilter.setOnClickListener {
            childFragmentManager.beginTransaction().apply {
                if(id == null ) {
                    id = FilterSearch::class
                    replace(R.id.fragment_filter, filterSearch).commit()
                } else{
                    id = null
                    remove(filterSearch).commit()
                }
            }

        }

        etSearchBar = requireView().findViewById(R.id.etSearchBar)
        btnAddNew = requireView().findViewById(R.id.add_new_item)
        rcv = requireView().findViewById(R.id.rec_view)

        //Views Logic
        recyclerviewSetup()

        btnAddNew.editNewVendItems(
            context = requireContext(),
            tempItems = tempItems,
            vendItems = vendItems,
            adapter = rcv.adapter as VendItemAdapter,
            code = VendButtonCode.NEW_VEND_ITEM)


        etSearchBar.searchVendItems(tempItems, vendItems, rcv)


        btnGeneratePdf.setOnClickListener {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PackageManager.PERMISSION_GRANTED)


            Dialog(requireContext()).run{
                setContentView(R.layout.popup_pdf_menu)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                show()

                val close = findViewById<TextView>(R.id.tvClosePdfMenu)
                val title = findViewById<EditText>(R.id.etPdfMenuInputTitle)
                val cbLabels = findViewById<TextView>(R.id.tvPdfMenuInputLabel)
                val btnConfirm = findViewById<Button>(R.id.btnConfirmPdfMenu)

                close.setOnClickListener { dismiss() }
                cbLabels.setOnClickShowLabelsCheckBox(requireContext())

                val labelList = mutableListOf<Labels>()

                btnConfirm.setOnClickListener {
                    val chosenLabels = cbLabels.text.split(", ")
                    Log.d("Chosen Labels", "$chosenLabels, ${chosenLabels[0]}")
                    val rc = requireContext()
                    when{
                        chosenLabels[0].isBlank()-> makeToast(rc, "Please Choose at least 1 Label")
                        chosenLabels.size > 4 -> makeToast(rc, "Too Much Labels")
                        else -> {
                            tinyDB.getAutoListObject<Labels>().forEach { itLabel ->
                                if(chosenLabels.any{it == itLabel.name}){
                                    labelList.add(itLabel)
                                }
                            }

                            val pdfMenu = PdfMenu(title.text.toString()
                                    .shortenToDots(21), labelList)
                            PDFConverter()
                                .createPdf(requireContext(), pdfMenu, requireActivity())
                            dismiss()
                        }
                    }

                }

            }

        }

    }


    private fun recyclerviewSetup(){
        rcv.setHasFixedSize(true)

        rcv.layoutManager = GridLayoutManager(context, 2)


        vendItems = tinyDB.getAutoListObject()
        tempItems = mutableListOf()
        tempItems.addAll(vendItems)
        vendItemAdapter = VendItemAdapter(requireContext(), tempItems, requireActivity(), vp2Revenue)
        rcv.adapter = vendItemAdapter
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        when (newConfig.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> rcv.layoutManager = GridLayoutManager(context, 4)
            Configuration.ORIENTATION_PORTRAIT -> rcv.layoutManager = GridLayoutManager(context, 2)
            else ->{}
        }

    }


}