package com.example.easySale.fragments

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.example.easySale.*
import com.example.easySale.adapters.LabelAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

import kotlin.collections.ArrayList

class Label : Fragment(R.layout.fragment_label) {
    private lateinit var rcv: RecyclerView
    private lateinit var labelAdapter : LabelAdapter
    private lateinit var labels : MutableList<Labels>
    private lateinit var fabNewLabel : FloatingActionButton
    private lateinit var tinyDB : TinyDB

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        tinyDB = TinyDB(context)

        fabNewLabel = requireView().findViewById(R.id.fabNewLabel)
        val dialog = Dialog(requireContext())
        rcv = requireView().findViewById(R.id.rcvLabel)

        rcvSetup()


        fabNewLabel
            .setLabelEditNewPopUp(dialog, labels as ArrayList<Labels>,
                labelAdapter, LabelButtonCode.NEW_LABEL_ITEM)




//        fabNewLabel.setOnClickListener {
//            dialog.setContentView(R.layout.popup_edit_label)
//            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            dialog.show()
//
//            val btnBgColor : Button = dialog.findViewById(R.id.btnLabelBgColor)
//            val btnTextColor : Button = dialog.findViewById(R.id.btnLabelTextColor)
//            val btnConfirm : Button = dialog.findViewById(R.id.btnConfirmEditLabel)
//            val tvCloseLabel : TextView = dialog.findViewById(R.id.tvCloseLabelEdit)
//            val name: TextView = dialog.findViewById(R.id.etLabelName)
//
//            var bgColorSaver = resources.getColor(R.color.dark_aqua)
//
//            tvCloseLabel.setOnClickListener{
//                dialog.dismiss()
//            }
//
//            btnTextColor.setOnClickListener {
//                Dialog(requireContext()).apply {
//                    setContentView(R.layout.popup_color)
//                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//                    show()
//
//                    findViewById<TextView>(R.id.closeColorLabel).setOnClickListener {
//                        dismiss()
//                    }
//
//                    findViewById<RecyclerView>(R.id.rvColor).run {
//                        setHasFixedSize(true)
//                        layoutManager = GridLayoutManager(context, 3)
//                        val arrayColor = resources.getIntArray(R.array.color_palette)
//                        adapter = ColorAdapter(context, arrayColor, this@apply)
//                    }
//                }.setOnCancelListener {
//                    requireContext().getSharedPreferences(LABEL_COLOR_DB, Context.MODE_PRIVATE)
//                        .getInt(LABEL_SAVING_COLOR, 0)
//                        .let {
//                            btnTextColor.background.setTint(bgColorSaver)
//                            btnTextColor.setTextColor(it)
//
//                            btnBgColor.background.setTint(bgColorSaver)
//                            btnBgColor.setTextColor(it)
//                        }
//                }
//            }
//
//            btnBgColor.setOnClickListener {
//                Dialog(requireContext()).apply {
//                    setContentView(R.layout.popup_color)
//                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//                    show()
//
//                    findViewById<TextView>(R.id.closeColorLabel).setOnClickListener {
//                        dismiss()
//                    }
//
//                    findViewById<RecyclerView>(R.id.rvColor).run{
//                        setHasFixedSize(true)
//                        layoutManager = GridLayoutManager(context, 3)
//                        val arrayColor = resources.getIntArray(R.array.color_palette)
//                        adapter = ColorAdapter(context, arrayColor, this@apply)
//                    }
//                }.setOnCancelListener {
//                    requireContext().getSharedPreferences(LABEL_COLOR_DB, Context.MODE_PRIVATE)
//                        .getInt(LABEL_SAVING_COLOR, 0)
//                        .let {
//                            btnBgColor.background.setTint(it)
//                            btnBgColor.setTextColor(btnBgColor.currentTextColor)
//                            btnTextColor.background.setTint(it)
//                            btnTextColor.setTextColor(btnTextColor.currentTextColor)
//                            bgColorSaver = it
//                        }
//                }
//            }
//
//            btnConfirm.setOnClickListener {
//                if(name.text.toString().trim().isBlank()){
//                    Toast.makeText(context, "Label's Name Cannot be Empty", Toast.LENGTH_SHORT).show()
//                } else {
//                    val label =
//                        Labels(name.text.toString(), bgColorSaver, btnTextColor.currentTextColor, )
//                    labels.add(label)
//                    tinyDB.putListObject(LABEL_DB, labels as ArrayList<Any>)
//                    rcv.adapter?.notifyDataSetChanged()
//                    dialog.dismiss()
//                }
//            }
//        }





    }

    private fun rcvSetup(){

        rcv.setHasFixedSize(true)

        LinearLayoutManager(context).also{
                it.orientation = VERTICAL
        }.also {
            rcv.layoutManager = it
        }

        labels  = tinyDB.getAutoListObject()
        labelAdapter = LabelAdapter(requireContext(), labels)
        rcv.adapter = labelAdapter
    }

}