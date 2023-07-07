package com.example.easySale

import android.graphics.Color
import java.util.*
import kotlin.collections.ArrayList

data class Items(
    var title:String,
    var price: Float,
    var label : ArrayList<Labels>? = null,
    var sold : Int = 0,
    var isDeleted : Boolean = false
)

data class Labels(
    var name:String,
    var backgroundColor: Int = Color.CYAN,
    var icon : String,
    var isDeleted : Boolean = false
)

data class Buyer(
    var name: String,
    var quantity : Int,
    var itemName: String,
    var costs : Float,
    // format : MM/dd/yyyy (bulan / hari / tahun)
    var date: Date,
    var labels : ArrayList<Labels>? = null,
    var isItemDeleted : Boolean = false
)

data class PdfMenu(
    var title : String,
    var labels : MutableList<Labels>
)

data class Summary(
    var name : String,
    var quantity: Int,
    var price: Float,
    var label : ArrayList<Labels>?
)