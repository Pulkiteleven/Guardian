package com.inertia.guardian.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.inertia.guardian.R
import com.inertia.guardian.models.contacts
import java.lang.reflect.Type

class contactadapter(private val mctx:Context,private val contactlist:ArrayList<contacts>) : RecyclerView.Adapter<contactViewHolder>() {
    private val mCtx:Context = mctx
    private val contactList:List<contacts> = contactlist

    public var favList:MutableList<contacts> = ArrayList()


    val sharedPreferences: SharedPreferences = mCtx.getSharedPreferences("con",
        AppCompatActivity.MODE_PRIVATE
    )
    val gsoon: Gson = Gson()
    val jsoon:String ?= sharedPreferences.getString("cons",null)

    init {
        if (jsoon != null){
            val type:Type = object :TypeToken<MutableList<contacts>>(){}.type
            favList = gsoon.fromJson(jsoon,type)
        }
    }



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): contactViewHolder {
        val view = LayoutInflater.from(mCtx).inflate(R.layout.contact_item,parent,false)
        return contactViewHolder(view,contactList)

    }

    override fun onBindViewHolder(holder: contactViewHolder, position: Int) {

        val c = contactList.get(position)

        holder.contactName?.setText(c.name)
        holder.no?.setText(c.no)

        for(x in 0..favList.size-1){
            if (favList[x].no.equals(c.no)){
                holder.check!!.visibility = View.VISIBLE
                break
            }
            else{
                holder.check!!.visibility = View.GONE
            }
        }

    }

    override fun getItemCount(): Int {
        return contactList.size

    }
}

class contactViewHolder(v:View,private val contactlist: List<contacts>):RecyclerView.ViewHolder(v),View.OnClickListener {
    var contactName:TextView ?= null
    var no:TextView ?= null
    var card:CardView ?= null
    var check:ImageView ?= null
    public var favList:MutableList<contacts> = ArrayList()


    init {
        contactName = v.findViewById(R.id.contact_name)
        no = v.findViewById(R.id.contact_no)
        card = v.findViewById(R.id.card)
        check = v.findViewById(R.id.check)
        v.setOnClickListener(this)


    }


    @SuppressLint("ResourceType")
    override fun onClick(p0: View?) {
        val i = adapterPosition
        val c:contacts = contactlist.get(i)
//        card?.setBackgroundTintList(p0?.context?.resources!!.getColorStateList(R.color.upl))

        val sharedPreferences: SharedPreferences = p0?.context!!.getSharedPreferences("con",
            AppCompatActivity.MODE_PRIVATE
        )
        val edit: SharedPreferences.Editor = sharedPreferences.edit()
        val gsoon: Gson = Gson()

        val jsoon:String ?= sharedPreferences.getString("cons",null)
        if (jsoon != null){
            val type:Type = object :TypeToken<MutableList<contacts>>(){}.type
             favList = gsoon.fromJson(jsoon,type)
        }
        var tf:Boolean = true
        val ii:contacts = contacts(c.name,c.no)
        for(x in 0..favList!!.size-1){
            if (favList!![x].no.equals(c.no)){
                tf = false
                break
            }
            else{
               tf = true
            }
        }
        if (tf){
            favList.add(ii)
            check?.visibility = View.VISIBLE
        }
        else{
            favList.remove(ii)
            check?.visibility = View.GONE
        }
        val gson:Gson = Gson()
        val json:String = gson.toJson(favList)
        edit.putString("cons",json)
        edit.apply()
    }
}
