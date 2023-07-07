package com.example.easySale.adapters

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.easySale.*
import java.util.ArrayList


class LabelAdapter(private val context: Context, private val labels: MutableList<Labels>)
    : RecyclerView.Adapter<LabelAdapter.ViewHolder>(){

    private val tinyDB = TinyDB(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.label_item, parent, false)
        )


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dialog = Dialog(context)

        holder.tvLabel.text = labels[position].name

        holder.cvLabel.background.setTint(labels[position].backgroundColor)
        holder.ivLabel.setImageDrawable(labels[position].icon.getDrawableByNameAssets(context))


        //initEditDialog(dialog,holder,position)

        holder.cvLabel
            .setLabelEditNewPopUp(dialog, labels as ArrayList<Labels>, this,
                LabelButtonCode.EDIT_LABEL_ITEM, position)


        holder.tvDeleteLabel.setOnClickListener {
            dialog.setContentView(R.layout.popup_delete)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

            val confirm = dialog.findViewById<Button>(R.id.btnConfirmDelete)
            val cancel = dialog.findViewById<Button>(R.id.btnCancelDelete)


            dialog.findViewById<TextView>(R.id.tvDelete).text = labels[position].name

            confirm.setOnClickListener {
                val vendList :MutableList<Items> = (tinyDB.getAutoListObject<Items>()).onEach {
                        it.label?.remove(labels[position])
                        if(it.label.isNullOrEmpty()){
                            it.label = null
                        }
                    }

                val buyerList : ArrayList<Buyer> = (tinyDB.getAutoListObject<Buyer>()).onEach {
                        if(!it.labels.isNullOrEmpty() && it.labels!!.contains(labels[position])){
                            it.labels!!.forEach {if(it == labels[position]) it.isDeleted=true}
                        }
                    }

                tinyDB.putListObject(BUYER_DB.currentAcc(tinyDB), buyerList as ArrayList<Any>)

                tinyDB.putListObject(VEND_DB.currentAcc(tinyDB), vendList as ArrayList<Any>)


                labels.removeAt(position)
                tinyDB.putListObject(LABEL_DB.currentAcc(tinyDB), labels as ArrayList<Any>)

                notifyDataSetChanged()
                //notifyItemRemoved(position)
                dialog.dismiss()

            }
            cancel.setOnClickListener {
                dialog.dismiss()
            }
        }


    }

    override fun getItemCount(): Int = labels.size


    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val tvLabel: TextView = itemView.findViewById(R.id.tvLabel)
        val ivLabel: ImageView = itemView.findViewById(R.id.ivLabelIcon)
        val cvLabel: CardView = itemView.findViewById(R.id.cvLabelIcon)
        val tvDeleteLabel: TextView = itemView.findViewById(R.id.tvDeleteLabel)

    }

//    private fun initEditDialog(dialog:Dialog, holder:ViewHolder, position:Int){
//        holder.tvLabel.setOnClickListener{
//            dialog.setContentView(R.layout.popup_edit_label)
//            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            dialog.show()
//
//            var bgColorSaver = labels[position].backgroundColor
//
//            val btnBgColor : Button = dialog.findViewById(R.id.btnLabelBgColor)
//            val cvItemIcon : CardView = dialog.findViewById(R.id.cvLabelItemIcon)
//            val ivItemIcon : ImageView = dialog.findViewById(R.id.ivLabelItemIcon)
//            val btnConfirm : Button = dialog.findViewById(R.id.btnConfirmEditLabel)
//            val tvCloseLabel : TextView = dialog.findViewById(R.id.tvCloseLabelEdit)
//            val name: EditText = dialog.findViewById(R.id.etLabelName)
//            name.setText(labels[position].name)
//
//            btnBgColor.background.setTint(labels[position].backgroundColor)
//
//
//            tvCloseLabel.setOnClickListener{
//                dialog.dismiss()
//            }
//
//
//            btnBgColor.setOnClickListener {
//                Dialog(context).apply {
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
//                    context.getSharedPreferences(LABEL_COLOR_DB, Context.MODE_PRIVATE)
//                        .getInt(LABEL_SAVING_COLOR, 0)
//                        .let {
//                            btnBgColor.background.setTint(it)
//                            btnBgColor.setTextColor(btnBgColor.currentTextColor)
//                            cvItemIcon.background.setTint(it)
//                            bgColorSaver = it
//                        }
//                }
//
//            }
//
//
//
//            cvItemIcon.setOnClickListener {
//                Dialog(context).apply {
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
//                        val iconList = getAllDrawablesNameFromAssets(context)
//                        adapter = IconAdapter(context, iconList, this@apply)
//                    }
//                }.setOnCancelListener {
//                    context.getSharedPreferences(LABEL_COLOR_DB, Context.MODE_PRIVATE)
//                        .getInt(LABEL_SAVING_COLOR, 0)
//                        .let {
//                            btnBgColor.background.setTint(it)
//                            btnBgColor.setTextColor(btnBgColor.currentTextColor)
//                            cvItemIcon.background.setTint(it)
//                            bgColorSaver = it
//                        }
//                    ivItemIcon.setImageDrawable(TinyDB(context)
//                        .getObject(ICON_LABELS, Drawable::class.java))
//                }
//            }
//
//
//            btnConfirm.setOnClickListener {
//                // val bgColor = btnTextColor.getBackgroundColor()
//                if(name.text.toString().trim().isBlank()){
//                    Toast.makeText(context, "Label's Name Cannot be Empty", Toast.LENGTH_SHORT).show()
//                } else {
//                    val label = labels[position].copy()
//                    labels[position].name = name.text.toString()
//                    labels[position].icon = TinyDB(context).getObject(ICON_LABELS, Drawable::class.java)
//                    labels[position].backgroundColor = bgColorSaver
//
//                    val vendList :MutableList<Items> = (tinyDB.getListObject(VEND_DB, Items::class.java)
//                        as MutableList<Items>).onEach{
//                            if(!it.label.isNullOrEmpty() && it.label!!.contains(label)) {
//                                it.label!!.remove(label)
//                                it.label!!.add(labels[position])
//                            }
//                        }
//
//                    tinyDB.putListObject(LABEL_DB, labels as ArrayList<Any>)
//                    tinyDB.putListObject(VEND_DB, vendList as ArrayList<Any>)
//                    notifyDataSetChanged()
//
//                    dialog.dismiss()
//                }
//
//            }
//        }
//    }


}