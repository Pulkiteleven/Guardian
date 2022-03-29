package com.inertia.guardian.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.inertia.guardian.Activity.PersonalReport
import com.inertia.guardian.R
import com.inertia.guardian.models.reports

class reportAdapter(private val mctx:Context,private val reportlist:ArrayList<reports>):RecyclerView.Adapter<reportViewHolder>() {
    private val mCtx:Context = mctx
    private val reportList:List<reports> = reportlist
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): reportViewHolder {
        val view = LayoutInflater.from(mCtx).inflate(R.layout.item_report,parent,false)
        return reportViewHolder(view,reportList)
    }

    override fun onBindViewHolder(holder: reportViewHolder, position: Int) {
        val c = reportList.get(position)

        holder.aname?.setText(c.name)
        holder.branch?.setText(c.branch)
        val a = "year : " + c.year
        holder.year?.setText(a)
    }

    override fun getItemCount(): Int {
        return reportList.size
    }
}

class reportViewHolder(v:View,private val reportlist:List<reports>):RecyclerView.ViewHolder(v), View.OnClickListener {
    var aname:TextView ?= null
    var branch:TextView ?= null
    var year:TextView ?= null
    var card:CardView ?= null

    init {
        aname = v.findViewById(R.id.name)
        branch = v.findViewById(R.id.branch)
        year = v.findViewById(R.id.year)
        card = v.findViewById(R.id.card)

        card?.setOnClickListener {
            val r = adapterPosition
            val i = Intent(it.context,PersonalReport::class.java)
            i.putExtra("name",reportlist.get(r).name)
            i.putExtra("year",reportlist.get(r).year)
            i.putExtra("branch",reportlist.get(r).branch)
            i.putExtra("grp",reportlist.get(r).grp)
            i.putExtra("grpinfo",reportlist.get(r).grpinfo)
            i.putExtra("desc",reportlist.get(r).desc)
            i.putExtra("evidence",reportlist.get(r).evidence as ArrayList<*>)
            i.putExtra("docid",reportlist.get(r).id)
            i.putExtra("col",reportlist.get(r).collection)
            it.context?.startActivity(i)
        }

    }

    override fun onClick(p0: View?) {
        val r = adapterPosition
        val i = Intent(p0?.context,PersonalReport::class.java)
        i.putExtra("name",reportlist.get(r).name)
        i.putExtra("year",reportlist.get(r).year)
        i.putExtra("branch",reportlist.get(r).branch)
        i.putExtra("grp",reportlist.get(r).grp)
        i.putExtra("grpinfo",reportlist.get(r).grpinfo)
        i.putExtra("desc",reportlist.get(r).desc)
        i.putExtra("evidence",reportlist.get(r).evidence as ArrayList<*>)

        p0?.context?.startActivity(i)
    }

}
