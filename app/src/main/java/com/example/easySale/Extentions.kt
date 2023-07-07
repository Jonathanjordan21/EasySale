package com.example.easySale

import android.animation.ObjectAnimator
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.*
import android.view.animation.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.easySale.adapters.VendItemAdapter
import java.lang.Exception
import java.util.*
import android.view.View.OnTouchListener
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import com.anychart.AnyChart
import com.anychart.AnyChart.vertical
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Cartesian
import com.anychart.core.cartesian.series.Bar
import com.anychart.core.cartesian.series.Column
import com.anychart.core.cartesian.series.JumpLine
import com.anychart.data.Set
import com.anychart.enums.*
import com.anychart.graphics.vector.Stroke
import com.example.easySale.adapters.ColorAdapter
import com.example.easySale.adapters.IconAdapter
import com.example.easySale.adapters.LabelAdapter
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


//fun View.getBackgroundColor() = (background as? ColorDrawable?)?.color ?: Color.TRANSPARENT

fun EditText.searchVendItems(tempItems : MutableList<Items>,
    vendItems : MutableList<Items>, rcv : RecyclerView) {

    val imm =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    setOnClickListener {
        compoundDrawables[DRAWABLE_RIGHT].setTint(Color.WHITE)
        isCursorVisible = if(!isCursorVisible){
            ObjectAnimator.ofFloat(this, "scaleX",0.3f, 1f, 0.5f, 1.5f, 1.2f).apply{
                interpolator = OvershootInterpolator()
                textSize = 16f - 1.2f
                duration = 300
            }.start()
            if(hint.isNullOrBlank()
                || hint.trim() == resources.getString(R.string.search_item_name)) {
                hint = ""
            }
            true
        } else {
            if(text.isNullOrBlank()
                || text.trim() == resources.getString(R.string.search_item_name)) {
                ObjectAnimator.ofFloat(this, "scaleX", 1.5f, 1.2f, 0.3f, 0.5f, 1f).apply {
                    interpolator = AnticipateOvershootInterpolator()
                    textSize = 16f
                    duration = 300
                }.start()
            }

            imm.hideSoftInputFromWindow(this.windowToken, 0)
            compoundDrawables[DRAWABLE_RIGHT].setVisible(false, false)
            hint = resources.getString(R.string.search_item_name)
            false
        }
    }

    setOnTouchListener(OnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_UP) {
            if (event.rawX >= right - compoundDrawables[DRAWABLE_RIGHT].bounds.width()
            ) {
                // your action here
                    setText("")
                imm.hideSoftInputFromWindow(this.windowToken, 0)
                compoundDrawables[DRAWABLE_RIGHT].setTint(Color.BLACK)
                ObjectAnimator.ofFloat(this, "scaleX", 1.5f, 1.2f, 0.3f, 0.5f, 1f).apply {
                    interpolator = AnticipateOvershootInterpolator()
                    textSize = 16f
                    duration = 300
                }.start()
                isCursorVisible = false
                hint = resources.getString(R.string.search_item_name)
                return@OnTouchListener true
            }
        }
        false
    })

    setOnEditorActionListener { _, _, event ->
        event?.let {
            if (event.keyCode == KeyEvent.KEYCODE_ENTER) {
                // hide virtual keyboard

                imm.hideSoftInputFromWindow(this.windowToken, 0)
                if(text.isNullOrBlank()
                    || text.trim() == resources.getString(R.string.search_item_name)) {
                    ObjectAnimator.ofFloat(this, "scaleX", 1.5f, 1.2f, 0.3f, 0.5f, 1f).apply {
                        interpolator = AnticipateOvershootInterpolator()
                        textSize = 16f
                        duration = 300
                    }.start()
                }
                compoundDrawables[DRAWABLE_RIGHT].setTint(Color.BLACK)
                isCursorVisible =  false
                return@setOnEditorActionListener true
            }
        }
        false
    }

    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            onTextChanged(s, start, count, after)
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s.isNullOrBlank()) {
                tempItems.clear()
                tempItems.addAll(vendItems)
                rcv.adapter!!.notifyDataSetChanged()
            } else {
                val searchChar = s.toString().trim().lowercase()
                tempItems.clear()
                tempItems.addAll(vendItems.filter {
                    it.title.lowercase().contains(searchChar)
                } as MutableList<Items>)
                rcv.adapter!!.notifyDataSetChanged()


            }
        }

        override fun afterTextChanged(s: Editable?) {}
    })
}


fun Button.editNewVendItems(context : Context, tempItems : MutableList<Items>? = null,
                            vendItems : MutableList<Items>, adapter: VendItemAdapter,
                            position : Int = -1, code : VendButtonCode)
{
    val dialog = Dialog(context)
    val tinyDB = TinyDB(context)
    setOnClickListener {
        dialog.setContentView(R.layout.popup_add_new)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()


        val name = dialog.findViewById<EditText>(R.id.etName)
        val price = dialog.findViewById<EditText>(R.id.etPrice)
        val label = dialog.findViewById<TextView>(R.id.tvVendLabel)
        val btnConfirm = dialog.findViewById<Button>(R.id.btnConfirmAdd)
        val tvCancel = dialog.findViewById<TextView>(R.id.tvCrossCancel)

        if(code == VendButtonCode.EDIT_VEND_ITEM){
            dialog.findViewById<TextView>(R.id.tvTitleEditNew)
                .setText(R.string.editing_item)
            name.setText(vendItems[position].title)
            price.setText(vendItems[position].price.toString())
        }

        val checkedLabel = arrayListOf<Int>()

        var labelInitEdit : ArrayList<Int>? = null

        if(code == VendButtonCode.EDIT_VEND_ITEM) {
            vendItems[position].label?.run {
                labelInitEdit = arrayListOf()
            }
        }

        lateinit var labelArray : Array<String>

        val labelsDB =
            (tinyDB.getAutoListObject<Labels>()).apply{
                if(code == VendButtonCode.EDIT_VEND_ITEM) {
                    vendItems[position].label?.run {
                        for(x in 0 until this.size) {
                            if (this@apply.contains(this[x])) {
                                labelInitEdit?.add(this@apply.indexOf(this[x]))
                            }
                        }
                    }
                }

                labelArray = Array(size){
                    this[it].name
                }
            }

        val selectedLabel = BooleanArray(labelArray.size)


        label.setOnClickListener {
            if(code == VendButtonCode.EDIT_VEND_ITEM) {
                if(label.text.isNullOrBlank()){
                    labelInitEdit?.forEach {
                        selectedLabel[it] = true
                    }
                }
            }
            AlertDialog.Builder(context).apply {
                setTitle("Choose Labels / Categories")
                setCancelable(false)

                if(code == VendButtonCode.EDIT_VEND_ITEM) {
                    if(label.text.isNullOrBlank()){
                        labelInitEdit?.forEach { if(selectedLabel[it])checkedLabel.add(it) }
                    }
                }

                setMultiChoiceItems(labelArray, selectedLabel) { _, which, isChecked ->

                    if (isChecked) {
                        checkedLabel.add(which)
                        checkedLabel.sort()
                    } else {
                        checkedLabel.remove(which)
                    }
                }



                setPositiveButton("OK") { _, _ ->
                    val labelsName = StringBuilder()
                    for (j in 0 until checkedLabel.size) {
                        labelsName.append(labelArray[checkedLabel[j]])
                        if (j != checkedLabel.size - 1) {
                            labelsName.append(", ")
                        }
                    }
                    label.text = labelsName.toString()
                }

                setNegativeButton("Cancel") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }

                setNeutralButton("Clear All") { _, _ ->
                    for (j in selectedLabel.indices) {
                        selectedLabel[j] = false
                        checkedLabel.clear()
                        label.text = ""
                    }
                }

            }.show()
        }



        btnConfirm.setOnClickListener {
            var prices = 0f
            if(!price.text.isNullOrBlank()) {
                prices = price.text.split("-").map {
                    it.trim().map { if (it == ',') '.' else it }.filter {
                        if (it.isWhitespace()) {
                            Toast.makeText(context, "Wrong Price Format!", Toast.LENGTH_SHORT)
                                .show()
                            return@setOnClickListener
                        }
                        it != '-'
                    }.map { it.code }.run {
                        StringBuilder().apply {
                            this@run.forEach { append(it.toChar()) }
                        }.toString().toFloat()
                    }
                }.run { foldIndexed(this[0]) { i, acc, v -> if (i > 0) acc - v else acc + 0 } }
            }

            val names = name.text.toString().lowercase()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            val nameList = tinyDB.getAutoListObject<Items>().map{it.title}
                .filter {
                    if (code == VendButtonCode.EDIT_VEND_ITEM)
                        it != vendItems[position].title
                    else true
                }

            when {
                name.text.isNullOrBlank() -> makeToast(context, "Name is Empty!")
                price.text.isNullOrBlank() -> makeToast(context, "Price is Empty!")
                prices == 0f -> makeToast(context, "Price is cannot be 0!")
                names.length > 29 -> makeToast(context, "Name is Too Long!")
                else -> {
                    val labels = labelsDB.filter {
                        label.text.split(", ").contains(it.name)
                    } as ArrayList<Labels>
                    val item = if (!labels.isNullOrEmpty()) {
                        Items(names, prices, labels)
                    } else {
                        Items(names, prices)
                    }

                    when (code) {
                        VendButtonCode.EDIT_VEND_ITEM -> {
                            if(prices != vendItems[position].price){
                                vendItems[position] = item
                            }
                            vendItems[position] = item.copy(sold = vendItems[position].sold)
                        }

                        VendButtonCode.NEW_VEND_ITEM -> {
                            vendItems.add(item)
                            tempItems!!.add(item)
                        }
                    }
                    tinyDB.putListObject(VEND_DB.currentAcc(tinyDB),
                        vendItems as ArrayList<Any>)
                    adapter.notifyDataSetChanged()
                    dialog.dismiss()
                }
            }

        }

        tvCancel.setOnClickListener {
            dialog.dismiss()
        }

    }
}


operator fun Date.minus(other: Date) : Long =
    TimeUnit.DAYS.convert(time - other.time, TimeUnit.MILLISECONDS)

//operator fun Date.compareTo(other :Date) : Boolean = TimeUnit.DAYS.run{
//    convert(time, TimeUnit.MILLISECONDS) > convert(other.time, TimeUnit.MILLISECONDS)
//}

fun ArrayList<Buyer>.filterBuyersByDays(dayStart:Long, dayEnd: Long) : List<Buyer> = filter{
    val total = TimeUnit.DAYS.convert(Date().time - it.date.time, TimeUnit.MILLISECONDS)
    total in dayStart..dayEnd

}


fun ArrayList<Buyer>.calculateCostsByDays(dayStart:Long, dayEnd: Long) : Int =
    (filterBuyersByDays(dayStart, dayEnd).map { it.costs * it.quantity} as ArrayList<Int>).sum()


fun View.setLabelEditNewPopUp(dialog:Dialog, labels : ArrayList<Labels>,
                              labelAdapter: LabelAdapter, code: LabelButtonCode,
                              position: Int = -1){
    val tinyDB = TinyDB(context)
    setOnClickListener{
        dialog.setContentView(R.layout.popup_edit_label)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        var bgColorSaver = if(code == LabelButtonCode.EDIT_LABEL_ITEM) labels[position].backgroundColor
            else resources.getColor(R.color.cream)

        val btnBgColor : Button = dialog.findViewById(R.id.btnLabelBgColor)
        val cvItemIcon : CardView = dialog.findViewById(R.id.cvEditLabelIcon)
        val ivItemIcon : ImageView = dialog.findViewById(R.id.ivEditLabelIcon)
        val btnConfirm : Button = dialog.findViewById(R.id.btnConfirmEditLabel)
        val tvCloseLabel : TextView = dialog.findViewById(R.id.tvCloseLabelEdit)
        val name: EditText = dialog.findViewById(R.id.etLabelName)


        if(code == LabelButtonCode.EDIT_LABEL_ITEM) {
            cvItemIcon.background.setTint(labels[position].backgroundColor)
            ivItemIcon.setImageDrawable(labels[position].icon.getDrawableByNameAssets(context))
            name.setText(labels[position].name)
            btnBgColor.background.setTint(labels[position].backgroundColor)
        } else {
            ivItemIcon.setImageDrawable("images/tag.png".getDrawableByNameAssets(context))
            tinyDB.putObject(ICON_LABELS, "images/tag.png")
        }


        tvCloseLabel.setOnClickListener{
            dialog.dismiss()
        }


        btnBgColor.setOnClickListener {
            Dialog(context).apply {
                setContentView(R.layout.popup_color)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                show()

                findViewById<TextView>(R.id.closeColorLabel).setOnClickListener {
                    dismiss()
                }

                findViewById<RecyclerView>(R.id.rvColor).run{
                    setHasFixedSize(true)
                    layoutManager = GridLayoutManager(context, 4)
                    val arrayColor = resources.getIntArray(R.array.color_palette)
                    adapter = ColorAdapter(context, arrayColor, this@apply)
                }
            }.setOnCancelListener {
                context.getSharedPreferences(LABEL_COLOR_DB, Context.MODE_PRIVATE)
                    .getInt(LABEL_SAVING_COLOR, 0)
                    .let {
                        btnBgColor.background.setTint(it)
                        btnBgColor.setTextColor(btnBgColor.currentTextColor)
                        cvItemIcon.background.setTint(it)
                        bgColorSaver = it
                    }
            }

        }



        cvItemIcon.setOnClickListener {
            Dialog(context).apply {
                setContentView(R.layout.popup_color)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                show()

                findViewById<TextView>(R.id.closeColorLabel).setOnClickListener {
                    dismiss()
                }

                findViewById<RecyclerView>(R.id.rvColor).run{
                    setHasFixedSize(true)
                    layoutManager = GridLayoutManager(context, 4)
                    val iconList = getAllDrawablesNameFromAssets(context)
                    adapter = IconAdapter(context, iconList, this@apply)
                }
            }.setOnCancelListener {

                val names = TinyDB(context).getObject(ICON_LABELS, String::class.java)
                ivItemIcon.setImageDrawable(names.getDrawableByNameAssets(context))
            }
        }


        btnConfirm.setOnClickListener {
            // val bgColor = btnTextColor.getBackgroundColor()
            val labelNameList = tinyDB.getAutoListObject<Labels>().map{it.name}
                .filter {
                    if (code == LabelButtonCode.EDIT_LABEL_ITEM)
                        it != labels[position].name
                    else true
                }
            val nameStr = name.text.toString().lowercase()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            when {
                nameStr.trim().isBlank() -> {
                    Toast.makeText(
                        dialog.context,
                        "Label's Name Cannot be Empty", Toast.LENGTH_SHORT
                    ).show()
                }
                nameStr.length > 16 -> {
                    Toast.makeText(
                        dialog.context,
                        "Label's Name Too Long", Toast.LENGTH_SHORT
                    ).show()
                }

                labelNameList.contains(nameStr) -> {
                    Toast.makeText(
                        dialog.context,
                        "Identical Name is Prohibited!", Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    if (code == LabelButtonCode.EDIT_LABEL_ITEM) {
                        val label = labels[position].copy()
                        val names = TinyDB(context).getObject(ICON_LABELS, String::class.java)
                        labels[position].name = nameStr
                        labels[position].icon = names
                        labels[position].backgroundColor = bgColorSaver

                        val vendList: MutableList<Items> =
                            (tinyDB.getAutoListObject<Items>()).onEach {
                                if (!it.label.isNullOrEmpty() && it.label!!.contains(label)) {
                                    it.label!!.remove(label)
                                    it.label!!.add(labels[position])
                                }
                            }

                        tinyDB.putListObject(VEND_DB.currentAcc(tinyDB),
                            vendList as ArrayList<Any>)


                        val buyerList : ArrayList<Buyer> =
                            (tinyDB.getAutoListObject<Buyer>()).onEach {
                            if(!it.labels.isNullOrEmpty() && it.labels!!.contains(labels[position])){
                                it.labels!!.forEach {if(it == labels[position]) it.isDeleted=true}
                            }
                        }

                        tinyDB.putListObject(BUYER_DB.currentAcc(tinyDB),
                            buyerList as ArrayList<Any>)



                    } else {
                        val names = TinyDB(context).getObject(ICON_LABELS, String::class.java)
                        val label = Labels(nameStr, bgColorSaver, names)
                        labels.add(label)
                    }

                    tinyDB.putListObject(LABEL_DB.currentAcc(tinyDB),
                        labels as ArrayList<Any>)

                    labelAdapter.notifyDataSetChanged()

                    dialog.dismiss()
                }
            }

        }
    }
}


fun getAllDrawablesNameFromAssets(context:Context): ArrayList<String>{
    return context.assets.list("images")!!.map{"images/$it"} as ArrayList
}

fun String.getDrawableByNameAssets(context: Context): Drawable =
    Drawable.createFromStream(context.assets.open(this), null)


fun getAllDrawablesFromAssets(context: Context) : ArrayList<Drawable>{
    val am = context.assets
    val files = am.list("images")

    val drawables : ArrayList<Drawable> = arrayListOf()

    files?.forEach{
        drawables.add(
            Drawable.createFromStream(am.open("images/$it"),
            null))
    }

    return drawables
}



//DropDown or CheckBoxes
fun TextView.setOnClickShowLabelsCheckBox(context : Context){

    val tinyDB = TinyDB(context)
    val checkedLabel = arrayListOf<Int>()

    val label = this


    lateinit var labelArray : Array<String>

    val labelsDB =
        (tinyDB.getAutoListObject<Labels>()).apply{

            labelArray = Array(size){
                this[it].name
            }
        }

    val selectedLabel = BooleanArray(labelArray.size)


    label.setOnClickListener {
        AlertDialog.Builder(context).apply {
            setTitle("Choose Labels / Categories")
            setCancelable(false)

            setMultiChoiceItems(labelArray, selectedLabel) { _, which, isChecked ->

                if (isChecked) {
                    checkedLabel.add(which)
                    checkedLabel.sort()
                } else {
                    checkedLabel.remove(which)
                }
            }


            setPositiveButton("OK") { _, _ ->
                val labelsName = StringBuilder()
                for (j in 0 until checkedLabel.size) {
                    labelsName.append(labelArray[checkedLabel[j]])
                    if (j != checkedLabel.size - 1) {
                        labelsName.append(", ")
                    }
                }
                label.text = labelsName.toString()
            }

            setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }

            setNeutralButton("Clear All") { _, _ ->
                for (j in selectedLabel.indices) {
                    selectedLabel[j] = false
                    checkedLabel.clear()
                    label.text = ""
                }
            }
        }.show()
    }

}


fun Number.pixelToDp(context : Context) : Int = TypedValue
        .applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, this.toFloat(),
            context.resources.displayMetrics)
        .toInt()

fun TextView.setDatePickerOnClick(context : Context){
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val pickerListener = DatePickerDialog.OnDateSetListener { view, years, months, dayOfMonth ->
        val month2 = months+1
        val date = "$month2/$dayOfMonth/$years"
        text = date
    }
    setOnClickListener {
        DatePickerDialog(context,
            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
            pickerListener,year,month,day).apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }.show()
    }
}

fun makeToast(context : Context, str: String,
              duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(context, str, duration).show()


inline fun <reified T> TinyDB.getAutoListObject() : ArrayList<T> = when(T::class.java) {
    Buyer::class.java -> getListObject(BUYER_DB.currentAcc(this), T::class.java) as ArrayList<T>
    Items::class.java -> getListObject(VEND_DB.currentAcc(this), T::class.java) as ArrayList<T>
    Labels::class.java -> getListObject(LABEL_DB.currentAcc(this), T::class.java) as ArrayList<T>
    Summary::class.java -> getListObject(SUMMARY_DB.currentAcc(this), T::class.java) as ArrayList<T>
    String::class.java -> getListObject(WALLET_DB, T::class.java) as ArrayList<T>
    else -> throw Exception("INVALID CLASS NAME")
}

inline fun <reified T> TinyDB.getAutoListObject(accName : String) : ArrayList<T> = when(T::class.java) {
    Buyer::class.java -> getListObject(BUYER_DB.currentAcc(accName), T::class.java) as ArrayList<T>
    Items::class.java -> getListObject(VEND_DB.currentAcc(accName), T::class.java) as ArrayList<T>
    Labels::class.java -> getListObject(LABEL_DB.currentAcc(accName), T::class.java) as ArrayList<T>
    Summary::class.java -> getListObject(SUMMARY_DB.currentAcc(accName), T::class.java) as ArrayList<T>
    String::class.java -> getListObject(WALLET_DB, T::class.java) as ArrayList<T>
    else -> throw Exception("INVALID CLASS NAME")
}


fun String.currentAcc(tinyDB: TinyDB) = this+"_${tinyDB.getString(CURRENT_WALLET)}"

fun String.currentAcc(accName : String)=this+"_$accName"




fun Date.getDaysOfMonth() : Int= when(month){
    0 -> 31
    1 -> if(year%400 == 0) 29
    else if(year%4 == 0 && year%100 != 0) 29
    else 28
    2 -> 31
    3 -> 30
    4 -> 31
    5 -> 30
    6 -> 31
    7 -> 31
    8 -> 30
    9 -> 31
    10 -> 30
    11 -> 31
    else -> throw Exception("Invalid Month")
}

fun Date.getDaysOfYear() : Int =
    if(year%400 == 0) 366
    else if(year%4 == 0 && year%100 != 0) 366
    else 365


fun getDaysOfWeek(context : Context) : String{
    val intDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    val resStringArray = context.resources.getStringArray(R.array.list_of_day_week)
    return when(intDay) {
        1-> resStringArray[0]
        2-> resStringArray[1]
        3-> resStringArray[2]
        4-> resStringArray[3]
        5-> resStringArray[4]
        6-> resStringArray[5]
        7-> resStringArray[6]
        else -> throw Exception("INVALID DAY OF WEEK")
    }
}


fun getDaysOfWeek(context : Context, day : Int) : String {
    val resStringArray = context.resources.getStringArray(R.array.list_of_day_week)
    return when (day) {
        1 -> resStringArray[0]
        2 -> resStringArray[1]
        3 -> resStringArray[2]
        4 -> resStringArray[3]
        5 -> resStringArray[4]
        6 -> resStringArray[5]
        7 -> resStringArray[6]
        else -> throw Exception("INVALID DAY OF WEEK")
    }
}




fun ArrayList<Buyer>.hashMapBuyerPrice() : HashMap<String, Float> =
    groupingBy { it.name }.fold(0f){init, v ->
        init + (v.costs*v.quantity)
    } as HashMap


fun ArrayList<Buyer>.hashMapItemPrice(context: Context) : HashMap<String, Float> {
    val hashmap : HashMap<String, Float> = hashMapOf()
    val buyerMap = groupingBy { it.itemName }
        .fold(0f){ init, v -> init + (v.costs*v.quantity) }

    val vendItemList = TinyDB(context).getAutoListObject<Items>()

    vendItemList.forEach { hashmap[it.title] = 0f}

    buyerMap.forEach{(k,v)->
        if(hashmap.containsKey(k)) hashmap[k] = v
    }

    return hashmap
}


fun String.shortenToDots(maxLength : Int) : String{
    var str = this
    if(length > maxLength){
        str = replaceRange(maxLength, length, "...")
    }
    return str
}


//fun generatePdf(activity: Activity, parent: ViewGroup){
//    ActivityCompat.requestPermissions(activity, arrayOf(
//        Manifest.permission.WRITE_EXTERNAL_STORAGE), PackageManager.PERMISSION_GRANTED)
//
//    val document = PdfDocument()
//
//    // create a page description
//    val  pageInfo = PdfDocument.PageInfo
//        .Builder(100, 100, 1).create();
//
//    // start a page
//    val page = document.startPage(pageInfo);
//
//    // draw something on the page
//    page.canvas.drawBitmap(bitmap, 0F, 0F, null)
//    document.finishPage(page)
//    val filePath = File(context.getExternalFilesDir(null), "bitmapPdf.pdf")
//    document.writeTo(FileOutputStream(filePath))
//    document.close()
//}

fun View.loadBitmap(width : Int, height : Int) : Bitmap {
    val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    val c = Canvas(b);
    draw(c);

    return b;
}

fun createPdf(activity : Activity){
    val windowManager : WindowManager =
        activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val displayMetrics = DisplayMetrics()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        activity.display?.getRealMetrics(displayMetrics)
        displayMetrics.densityDpi
    } else {
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
    }

    val width = displayMetrics.widthPixels
    val height = displayMetrics.heightPixels

    val docs = PdfDocument()
//    val pageInfo = PdfDocument.PageInfo.buil
}



fun ArrayList<Buyer>.filterBuyerList(reportType : ReportType ) : List<Buyer> = when(reportType){
    ReportType.DAILY -> filter { it.date.date == Date().date &&
            it.date.year == Date().year && it.date.month == Date().month
    }
    ReportType.MONTHLY -> filter {
        it.date.year == Date().year && it.date.month == Date().month
    }
    ReportType.ANNUAL -> filter { it.date.year == Date().year }
}

fun ArrayList<Buyer>.labelIncomeHashMap(labelList : ArrayList<Labels>) : HashMap<Labels, Float>{

    val hashmap = hashMapOf<Labels, Float>()

    labelList.forEach { hashmap[it] = 0f}
    val labelNull = Labels("NoCategory", Color.RED, "tag.png")
    hashmap[labelNull] = 0f

    forEach{ itBuyer ->
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

fun ArrayList<Buyer>.labelQuantityHashMap(labelList : ArrayList<Labels>) : HashMap<Labels, Int>{

    val hashmap = hashMapOf<Labels, Int>()

    labelList.forEach { hashmap[it] = 0}
    val labelNull = Labels("NoCategory", Color.RED, "tag.png")
    hashmap[labelNull] = 0

    forEach{ itBuyer ->
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

fun ArrayList<Buyer>.productIncomeHashMap(itemsList : ArrayList<Items>) : HashMap<String, Float>{
    val hashmap = hashMapOf<String, Float>()

    itemsList.forEach { hashmap[it.title] = 0f}

    forEach{
        if(hashmap.containsKey(it.itemName)){
            hashmap[it.itemName] = hashmap[it.itemName]!! + it.quantity * it.costs
        }
    }
    return hashmap
}

fun ArrayList<Buyer>.productQuantityHashMap(itemsList : ArrayList<Items>) : HashMap<String, Int>{
    val hashmap = hashMapOf<String, Int>()

    itemsList.forEach { hashmap[it.title] = 0}

    forEach{
        if(hashmap.containsKey(it.itemName)){
            hashmap[it.itemName] = hashmap[it.itemName]!! + it.quantity
        }
    }
    return hashmap
}


fun ArrayList<Buyer>.dateIncomeHashMap(reportType: ReportType): HashMap<Int, Float?> {
    val hashmap: HashMap<Int, Float?> = hashMapOf()
    val bmap : Map<Date, Float> = groupingBy { it.date }.fold(0f)
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


fun AnyChartView.setUpVertChart(context: Context,chartType : ChartType,
                                buyerList : ArrayList<Buyer>, data  : ArrayList<DataEntry>){
    val vertical: Cartesian = vertical()

    vertical.background().fill("black")

    vertical.yAxis(0).title("Quantity")

    if(chartType == ChartType.PRODUCT_QUANTITY) {
        vertical.animation(true)
            .title("Sold Quantity of Products")
        vertical.xAxis(0).title("Product")
        val list = buyerList.productQuantityHashMap(TinyDB(context).getAutoListObject())
        list.forEach { (k, v) ->
            var name = k
            if (k.length > 10) {
                name = k.replaceRange(10, k.length, "...")
            }
            data.add(ValueDataEntry(name, v))
        }
    } else if(chartType == ChartType.LABEL_QUANTITY){
        vertical.animation(true)
            .title("Sold Quantity of Labels")
        vertical.xAxis(0).title("Label/Category")
        val list = buyerList.labelQuantityHashMap(TinyDB(context).getAutoListObject())
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
    setChart(vertical);
}




fun AnyChartView.setUpColumnChart(context: Context,chartType : ChartType,
                                  buyerList : ArrayList<Buyer>, data  : ArrayList<DataEntry>){
    data.clear()

    val cartesian: Cartesian = AnyChart.column()

    cartesian.background().fill("black");


    if(chartType == ChartType.LABEL_INCOME) {
        cartesian.xAxis(0).title("Label/Category")
        cartesian.yAxis(0).title("Income")
        cartesian.title("Income By Labels")
        val hashmap = buyerList.labelIncomeHashMap(TinyDB(context).getAutoListObject())
        hashmap.forEach{(k,v)->
            var name = k.name
            if(name.length > 10) {
                name = k.name.replaceRange(10, name.length, "...")
            }
            data.add(ValueDataEntry(name, v))
        }
    } else if(chartType == ChartType.PRODUCT_INCOME){
        cartesian.xAxis(0).title("Product")
        cartesian.yAxis(0).title("Income")
        cartesian.title("Income By Products")
        val hashmap = buyerList.productIncomeHashMap(TinyDB(context).getAutoListObject())
        hashmap.forEach{(k,v)->
            var name = k
            if(k.length > 10) {
                name = k.replaceRange(10, name.length, "...")
            }
            data.add(ValueDataEntry(name, v))
        }
    }



    val column: Column = cartesian.column(data)

    column.tooltip()
        .titleFormat("{%X}")
        .position(Position.CENTER_BOTTOM)
        .anchor(Anchor.CENTER_BOTTOM)
        .offsetX(0.0)
        .offsetY(5.0)
        .format("Income : {%Value}{groupsSeparator: }")

    cartesian.animation(true)


    cartesian.yScale().minimum(0.0)

    cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }")

    cartesian.tooltip().positionMode(TooltipPositionMode.POINT)
    cartesian.interactivity().hoverMode(HoverMode.BY_X)



    setChart(cartesian)
}



fun AnyChartView.setupLineChart2(context: Context, chartType : ChartType,
                                 reportType: ReportType, buyerList : ArrayList<Buyer>,
                                 data : ArrayList<DataEntry>){
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
    cartesian.yAxis(0).title("Income")

    if(reportType == ReportType.MONTHLY) {
        cartesian.title("Daily Income per Month")
        cartesian.xAxis(0).labels().padding(5.0, 5.0, 5.0, 5.0)
        cartesian.xAxis(0).title("Day Of Month")

    } else if(reportType == ReportType.ANNUAL){
        cartesian.title("Monthly Income per Year")
        cartesian.xAxis(0).labels().padding(5.0, 5.0, 5.0, 5.0)
        cartesian.xAxis(0).title("Month")
    }

    if(chartType == ChartType.ANNUAL_INCOME){
        cartesian.title("Annual Income")
        cartesian.xAxis(0).labels().padding(5.0, 5.0, 5.0, 5.0)
        cartesian.xAxis(0).title("Year")
    }



    val hashmap : HashMap<Int, Float?> = if(chartType == ChartType.ANNUAL_INCOME) {
        buyerList.groupingBy { it.date.year+1900 }.fold(0f) { init, v ->
            init + v.costs * v.quantity
        } as HashMap<Int, Float?>
    } else buyerList.dateIncomeHashMap(reportType)


    hashmap.forEach{ (k,v) -> data.add(ValueDataEntry("$k",v)) }


    val set = Set.instantiate()
    set.data(data)
    val series1Mapping = set.mapAs("{ x: 'x', value: 'value' }")


    val series1 = cartesian.line(series1Mapping)
    series1.name("Income")
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

    cartesian.legend().enabled(true)
    cartesian.legend().fontSize(13.0)
    cartesian.legend().padding(0.0, 0.0, 10.0, 0.0)

    setChart(cartesian)

}