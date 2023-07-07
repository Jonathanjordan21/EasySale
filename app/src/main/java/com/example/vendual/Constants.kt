package com.example.vendual

const val LABEL_DB = "LabelsList"
const val DELETED_BUYER_DB = "DeletedBuyerLabel"
const val VEND_DB = "VendItemsList"
const val BUYER_DB = "BuyerList"
const val WALLET_DB = "AccountList"
const val SUMMARY_DB = "SummaryList"


const val LABEL_COLOR_DB  = "LabelColor"
const val LABEL_SAVING_COLOR = "color"
const val ICON_LABELS = "iconLabels"

const val CURRENT_WALLET = "currentWalletsL"


const val REQUEST_CAMERA_CODE = 100
const val RESULT_IMAGE_CAPTURE = 1

const val RESULT_OK = -1
const val RESULT_CANCELLED = 0
const val RESULT_FIRST_USER = 1


//Drawable Position Const in View
const val DRAWABLE_LEFT = 0
const val DRAWABLE_TOP = 1
const val DRAWABLE_RIGHT = 2
const val DRAWABLE_BOTTOM = 3

enum class VendButtonCode{
    EDIT_VEND_ITEM,
    NEW_VEND_ITEM
}

enum class BarChartCode{
    Buyer, VendItem
}

enum class LabelButtonCode {
    EDIT_LABEL_ITEM,
    NEW_LABEL_ITEM
}

enum class ChartType{
    LABEL_INCOME, PRODUCT_INCOME,
    LABEL_QUANTITY, PRODUCT_QUANTITY,
    DEFAULT_INCOME, ANNUAL_INCOME
}

enum class ReportType{
    DAILY, MONTHLY, ANNUAL
}


enum class Attributes{
    BUYER, PRODUCT, LABEL, INCOME
}


enum class PaperFormat(val width : Int, val height : Int){
    A4(595, 842),
    LEGAL(563,975),
    TABLOID(750,1200),
    F4(793 ,1247)
}