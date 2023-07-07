package com.example.easySale.activities

import android.app.Dialog
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.easySale.*
import com.example.easySale.adapters.AccountAdapter
import com.google.android.material.snackbar.Snackbar
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import java.util.*
import kotlin.collections.ArrayList

class LoginActivity: AppCompatActivity() {
    private lateinit var rcv : RecyclerView
    private lateinit var tvCreate : TextView
    private lateinit var accList : ArrayList<String>
    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var tinyDB: TinyDB


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        tinyDB = TinyDB(this)
        tinyDB.remove(BUYER_DB)
        tinyDB.remove(VEND_DB)
        tinyDB.remove(LABEL_DB)
        tinyDB.remove(SUMMARY_DB)

        rcv = findViewById(R.id.rcvAccount)
        tvCreate = findViewById(R.id.tvCreateAcc)
        accList = tinyDB.getAutoListObject()
        rcv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        rcv.adapter = AccountAdapter(this, accList)

        tvCreate.setOnClickListener {
            Dialog(this).apply{
                setContentView(R.layout.popup_account)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                show()

                val etName = findViewById<EditText>(R.id.etAccName)
                etName.hint = ""
                val btnConfirm = findViewById<Button>(R.id.btnConfirmAcc)
                findViewById<TextView>(R.id.tvAccCancel).setOnClickListener {
                    dismiss()
                }


                btnConfirm.setOnClickListener {
                    val name = etName.text.toString().lowercase()
                        .replaceFirstChar { if (it.isLowerCase())
                            it.titlecase(Locale.getDefault())
                        else it.toString() }.shortenToDots(26)

                    when{
                        name.isBlank() -> makeToast(context, "Name is Empty!")
                        accList.contains(name) -> makeToast(context, "Cannot have Duplicate Name")
                        else -> {
                            accList.add(name)
                            tinyDB.putListObject(WALLET_DB, accList as ArrayList<Any>)
                            rcv.adapter?.notifyDataSetChanged()
                            dismiss()
                        }
                    }

                }

            }
        }

        itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(rcv)

    }

    var strDeleted : String? = null

    private val simpleCallback = object : ItemTouchHelper
    .SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN or
            ItemTouchHelper.START or ItemTouchHelper.END, ItemTouchHelper.LEFT){

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {

            val fromPos = viewHolder.adapterPosition
            val toPos = target.adapterPosition

            Collections.swap(accList, fromPos, toPos)
            tinyDB.putListObject(WALLET_DB, accList as ArrayList<Any>)

            recyclerView.adapter?.notifyItemMoved(fromPos, toPos)


            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val pos = viewHolder.adapterPosition
            strDeleted = accList[pos]
            accList.removeAt(pos)
            tinyDB.putListObject(WALLET_DB, accList as ArrayList<Any>)
            rcv.adapter?.notifyDataSetChanged()

            val name = viewHolder.itemView
                .findViewById<TextView>(R.id.tvAccountName).text.toString()

            val itemsList = tinyDB.getAutoListObject<Items>(name)
            val buyerList = tinyDB.getAutoListObject<Buyer>(name)
            val labelList = tinyDB.getAutoListObject<Labels>(name)

            tinyDB.remove(VEND_DB.currentAcc(name))
            tinyDB.remove(LABEL_DB.currentAcc(name))
            tinyDB.remove(BUYER_DB.currentAcc(name))

            Snackbar.make(rcv, strDeleted!!, Snackbar.LENGTH_LONG)
                .setAction("Undo"
                ) {
                    accList.add(pos, strDeleted!!)
                    tinyDB.putListObject(WALLET_DB, accList as ArrayList<Any>)
                    tinyDB.putListObject(VEND_DB.currentAcc(name), itemsList as ArrayList<Any>)
                    tinyDB.putListObject(LABEL_DB.currentAcc(name), labelList as ArrayList<Any>)
                    tinyDB.putListObject(BUYER_DB.currentAcc(name), buyerList as ArrayList<Any>)
                    rcv.adapter?.notifyDataSetChanged()
                }.show()

        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            RecyclerViewSwipeDecorator.Builder(this@LoginActivity, c,
                recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addSwipeLeftBackgroundColor(Color.RED)
                .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                .create()
                .decorate()


            super.onChildDraw(c, recyclerView, viewHolder,
                dX, dY, actionState, isCurrentlyActive)
        }

    }

}