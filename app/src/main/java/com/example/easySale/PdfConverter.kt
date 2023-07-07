package com.example.easySale

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.easySale.adapters.PdfMenuAdapter
import com.example.easySale.PaperFormat.*
import java.io.File
import java.io.FileOutputStream
import com.example.easySale.ChartType.*
import java.util.*
import kotlin.collections.ArrayList

class PDFConverter {

    private fun createBitmapFromView(
        context: Context,
        view: View,
        pdfMenu: PdfMenu,
        adapters: MutableList<PdfMenuAdapter>,
        activity: Activity
    ): Bitmap {
//        val studentName = view.findViewById<TextView>(R.id.txt_student_name)
//        val totalMarks = view.findViewById<TextView>(R.id.txt_total_marks)
//        val recyclerView = view.findViewById<RecyclerView>(R.id.pdf_marks)
//        studentName.text = pdfDetails.studentName
//        totalMarks.text = pdfDetails.totalMarks.toString()
//        recyclerView.adapter = adapter

        val title = view.findViewById<TextView>(R.id.tvPdfMenuTitle)

        val tvLabelArray = arrayListOf<TextView>()
        tvLabelArray.add(view.findViewById(R.id.tvPdfMenuLabel1))
        tvLabelArray.add(view.findViewById(R.id.tvPdfMenuLabel2))
        tvLabelArray.add(view.findViewById(R.id.tvPdfMenuLabel3))
        tvLabelArray.add(view.findViewById(R.id.tvPdfMenuLabel4))

        val tvLabelIconArray = arrayListOf<ImageView>()
        tvLabelIconArray.add(view.findViewById(R.id.ivPdfMenuLabelIcon1))
        tvLabelIconArray.add(view.findViewById(R.id.ivPdfMenuLabelIcon2))
        tvLabelIconArray.add(view.findViewById(R.id.ivPdfMenuLabelIcon3))
        tvLabelIconArray.add(view.findViewById(R.id.ivPdfMenuLabelIcon4))


        val rcvArray = arrayListOf<RecyclerView>()
        rcvArray.add(view.findViewById<RecyclerView>(R.id.rcvPdfMenu1))
        rcvArray.add(view.findViewById<RecyclerView>(R.id.rcvPdfMenu2))
        rcvArray.add(view.findViewById<RecyclerView>(R.id.rcvPdfMenu3))
        rcvArray.add(view.findViewById<RecyclerView>(R.id.rcvPdfMenu4))

        for(x in 0..3){
            rcvArray[x].visibility = RecyclerView.GONE
            tvLabelIconArray[x].visibility = ImageView.GONE
            tvLabelArray[x].visibility = TextView.GONE
        }


        title.text = pdfMenu.title
        pdfMenu.labels.forEachIndexed{ i, v ->
            tvLabelIconArray[i].visibility = ImageView.VISIBLE
            tvLabelArray[i].visibility = TextView.VISIBLE
            tvLabelArray[i].text = v.name
            tvLabelIconArray[i].setImageDrawable(v.icon.getDrawableByNameAssets(context))
        }

        adapters.forEachIndexed{ i, v ->
            rcvArray[i].visibility = RecyclerView.VISIBLE
            rcvArray[i].layoutManager = GridLayoutManager(context, 1)
            rcvArray[i].adapter = v
        }


        return createBitmap(context, view, activity)
    }

    fun createBitmap(
        context: Context,
        view: View,
        activity: Activity,
        width : Int = 0,
        height : Int = 0
    ): Bitmap {
        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.display?.getRealMetrics(displayMetrics)
            displayMetrics.densityDpi
        } else {
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        }
        view.measure(
            View.MeasureSpec.makeMeasureSpec(
                displayMetrics.widthPixels, View.MeasureSpec.EXACTLY
            ),
            View.MeasureSpec.makeMeasureSpec(
                displayMetrics.heightPixels, View.MeasureSpec.EXACTLY
            )
        )
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        val bitmap = Bitmap.createBitmap(
            view.measuredWidth,
            view.measuredHeight, Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return if(width == 0 || height == 0)
                Bitmap.createScaledBitmap(bitmap, F4.width, F4.height, true)
            else Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

    fun convertBitmapToPdf(bitmap: Bitmap, context: Context, title: String) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        page.canvas.drawBitmap(bitmap, 0F, 0F, null)
        pdfDocument.finishPage(page)
        val filePath = File(context.getExternalFilesDir(null), "$title.pdf")
        pdfDocument.writeTo(FileOutputStream(filePath))
        pdfDocument.close()
        renderPdf(context, filePath)
    }

    fun createPdf(
        context: Context,
        pdfMenu: PdfMenu,
        activity: Activity
    ) {
        val inflater = LayoutInflater.from(context)
        val view  = inflater.inflate(R.layout.menu_pdf, null)
        val dataItemList = TinyDB(activity).getAutoListObject<Items>()
        val itemList = arrayListOf<ArrayList<Items>>()

        pdfMenu.labels.forEach{ itLabel ->
            val aList = arrayListOf<Items>()

            dataItemList.let{
                for(i in it.indices){
                    //if(i > 10) break

                    if(!it[i].label.isNullOrEmpty() && it[i].label!!.contains(itLabel)){
                        aList.add(it[i])
                    }
                }
            }

            itemList.add(aList)
        }

        //assert(itemList.size <= 4)

        val adapters = arrayListOf<PdfMenuAdapter>()

        itemList.forEach {
            adapters.add(PdfMenuAdapter(activity, it))
        }


        //val adapter = MarksRecyclerAdapter(pdfDetails.subjectDetailsList)
        val bitmap = createBitmapFromView(context, view, pdfMenu, adapters, activity)
        convertBitmapToPdf(bitmap, activity, pdfMenu.title.trim('/','\\','.',','))
    }


    private fun renderPdf(context: Context, filePath: File) {
        val uri = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".provider",
            filePath
        )
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(uri, "application/pdf")

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {

        }
    }
}