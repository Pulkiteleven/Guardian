
package com.inertia.guardian.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.inertia.guardian.MainActivity
import com.inertia.guardian.R
import com.inertia.guardian.adapters.evidenceAdapter
import com.inertia.guardian.adapters.reportAdapter
import kotlin.collections.ArrayList

class PersonalReport : AppCompatActivity() {

    var adapter:evidenceAdapter ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_report)

        supportActionBar?.hide()

        val names = intent.getStringExtra("name")
        val years = intent.getStringExtra("year")
        val branchs = intent.getStringExtra("branch")
        val grps = intent.getBooleanExtra("grp",false)
        val grpinfos = intent.getStringExtra("grpinfo")
        val descs = intent.getStringExtra("desc")
        val id = intent.getStringExtra("docid")
        val col = intent.getStringExtra("col")
        val evidenceList = intent.getIntegerArrayListExtra("evidence") as ArrayList<String>
        evidenceList.removeAt(0)

        val name:TextView = findViewById(R.id.name)
        val year:TextView = findViewById(R.id.year)
        val branch:TextView = findViewById(R.id.branch)
        val group:TextView = findViewById(R.id.group)
        val groupinfo:TextView = findViewById(R.id.groupinfo)
        val desc:TextView = findViewById(R.id.desc)
        val rc:LinearLayout = findViewById(R.id.grpp)
        val back:FloatingActionButton = findViewById(R.id.back)
        val evidences:RecyclerView = findViewById(R.id.evidences)
        val delete:FloatingActionButton = findViewById(R.id.delete)
        evidences.layoutManager = GridLayoutManager(this,1)
        adapter = evidenceAdapter(this, evidenceList,names!!)
        evidences.adapter = adapter

        val db:FirebaseFirestore = FirebaseFirestore.getInstance()

        name.setText(names)
        year.setText(years)
        branch.setText(branchs)
         if (grps){
             group.setText("Yes")
             groupinfo.setText(grpinfos)
         }
        else{
            group.setText("No")
             rc.visibility = View.GONE
        }
        desc.setText(descs)

        back.setOnClickListener{
            onBackPressed()
        }

        delete.setOnClickListener{
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Delete")
            builder.setMessage("You Want to Delete This Report")

            builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                db.collection(col!!).document(id!!)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Report Deleted Succesfully", Toast.LENGTH_SHORT).show()
                        onBackPressed()
                    }

            }

            builder.setNegativeButton(android.R.string.no) { dialog, which ->

            }


            builder.show()
        }
    }
}