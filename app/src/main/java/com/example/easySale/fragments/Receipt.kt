package com.example.easySale.fragments

import android.Manifest
import android.app.Dialog
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.easySale.*
import com.example.easySale.adapters.ReceiptItemAdapter
import com.example.easySale.adapters.ReceiptSummaryAdapter
import java.util.*
import kotlin.collections.ArrayList

class Receipt : Fragment(R.layout.fragment_receipt) {
    private lateinit var total: TextView
    private lateinit var rcvItem: RecyclerView
    private lateinit var rcvSummary: RecyclerView
    private lateinit var btnConfirm : Button
    private lateinit var btnClear : Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val tinyDB = TinyDB(requireContext())
        //val summaries = arrayListOf<Summary>()


        total = requireView().findViewById(R.id.tvTotal)
        rcvItem = requireView().findViewById(R.id.rcvItem)
        rcvSummary = requireView().findViewById(R.id.rcvSummary)
        btnClear = requireView().findViewById(R.id.btnClear)
        btnConfirm = requireView().findViewById(R.id.btnConfirm)


        btnConfirm.setOnClickListener {
            val summary = tinyDB.getAutoListObject<Summary>()
            if(summary.isEmpty()){
                makeToast(requireContext(), "Cannot Be Empty!")
                return@setOnClickListener
            }
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.popup_sales)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

            val confirm = dialog.findViewById<Button>(R.id.btnConfirmSales)
            val cancel = dialog.findViewById<TextView>(R.id.cancelIconSales)
            val extCustName = dialog.findViewById<EditText>(R.id.etxtCustName)
            dialog.findViewById<EditText>(R.id.etQuantity).visibility = TextView.GONE



            confirm.setOnClickListener {
                val custName =
                    if (extCustName.text.isNullOrBlank()) {
                        requireContext().resources.getString(R.string.unknown)
                    } else {
                        extCustName.text.toString().lowercase()
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                    }


                summary.forEach {
                    val buyerList = tinyDB.getAutoListObject<Buyer>()
                    val vendItems = tinyDB.getAutoListObject<Items>()
                    val date = Date()
                    val buyer = Buyer(
                        custName, it.quantity, it.name,
                        it.price, date, it.label
                    )

                    buyerList.add(buyer)
                    tinyDB.putListObject(
                        BUYER_DB.currentAcc(tinyDB),
                        buyerList as ArrayList<Any>
                    )

                    for(x in vendItems.indices){
                        if(vendItems[x].title == it.name){
                            vendItems[x].sold += it.quantity
                            break
                        }
                    }

                    tinyDB.putListObject(
                        VEND_DB.currentAcc(tinyDB),
                        vendItems as ArrayList<Any>
                    )

                }

                dialog.dismiss()
                generateReceiptPdf(summary)
                btnClear.performClick()
            }

            cancel.setOnClickListener {
                dialog.dismiss()
            }

        }

        btnClear.setOnClickListener {
            TinyDB(requireContext())
                .remove(SUMMARY_DB.currentAcc(TinyDB(requireContext())))
            rcvSummary.adapter = ReceiptSummaryAdapter(requireContext(), arrayListOf())
            rcvItem.adapter = ReceiptItemAdapter(requireContext(), rcvSummary, total)
            total.text = "Total : Rp 0"
        }

        rcvSummary.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.HORIZONTAL, false)

        rcvSummary.adapter = ReceiptSummaryAdapter(requireContext(), arrayListOf())
        rcvSummary.itemAnimator = DefaultItemAnimator()

        rcvItem.setHasFixedSize(true)
        rcvItem.layoutManager = GridLayoutManager(context, 2)

        rcvItem.adapter = ReceiptItemAdapter(requireContext(), rcvSummary, total)


        total.text = "Total : Rp 0"

    }


    private fun generateReceiptPdf(summary : ArrayList<Summary>){
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            PackageManager.PERMISSION_GRANTED
        )


        val receiptPdf = ReceiptPdf(summary, total.text)

        parentFragmentManager.beginTransaction().apply{
            replace(R.id.fragmentContainer, receiptPdf).commit()
        }

        Dialog(requireContext()).run{
            setContentView(R.layout.popup_report_pdf)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()

            val btnCancel = findViewById<Button>(R.id.btnCancelDownload)
            val btnDownload = findViewById<Button>(R.id.btnDownloadPdfReport)
            btnDownload.text = resources.getString(R.string.create_receipt)
            parentFragmentManager.beginTransaction().apply {
                btnCancel.setOnClickListener {
                    this.replace(R.id.fragmentContainer, Receipt()).commit()
                    dismiss()
                }

                btnDownload.setOnClickListener {
                    val bitmap = PDFConverter().createBitmap(
                        receiptPdf.requireContext(),
                        receiptPdf.requireView(),
                        receiptPdf.requireActivity(),
                        411f.pixelToDp(context), 718f.pixelToDp(context)
                    )
                    val d = Date()
                    val fileName =
                        "(${d.date}_${d.month + 1}_${d.year + 1900})_${"Receipt"}_REPORT"

                    PDFConverter().convertBitmapToPdf(
                        bitmap,
                        receiptPdf.requireContext(),
                        fileName,
                    )

                    this.replace(R.id.fragmentContainer, Receipt()).commit()
                    dismiss()

                }
            }

        }

    }

}