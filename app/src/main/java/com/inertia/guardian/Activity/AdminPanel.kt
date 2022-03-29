package com.inertia.guardian.Activity

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.inertia.guardian.MainActivity
import com.inertia.guardian.NavActivity.About
import com.inertia.guardian.NavActivity.Developer
import com.inertia.guardian.NavActivity.PrivacyPolicy
import com.inertia.guardian.R
import com.inertia.guardian.adapters.reportAdapter
import com.inertia.guardian.models.reports

class AdminPanel : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    var opener:Boolean = true
    var auth:FirebaseAuth ?= null
    var db:FirebaseFirestore ?= null
    var drawerLayout: DrawerLayout?= null
    var s:String ?= null
    var reportList:MutableList<reports> ?= null
    var adapter:reportAdapter ?= null
    var link:String ?= null
    var links:String ?= null
    var story:String ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_panel)

        s = intent.getStringExtra("college")

        supportActionBar?.hide()
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        reportList = ArrayList()

        val nav: FloatingActionButton = findViewById(R.id.navigation)
        val profile: MaterialButton = findViewById(R.id.profile_btn)
        val collegeName:TextView = findViewById(R.id.college_name)
        drawerLayout = findViewById(R.id.drawer_layout)
        val navi:NavigationView = findViewById(R.id.nav_view)
        val reports:RecyclerView = findViewById(R.id.reports)
        reports.layoutManager = GridLayoutManager(this,1)
        adapter = reportAdapter(this, reportList as ArrayList<reports>)
        reports.adapter = adapter


        collegeName.text = s
        val ref = "report " + s

        nav.setOnClickListener {
            drawerLayout?.openDrawer(GravityCompat.START)
        }
        navi.setNavigationItemSelectedListener(this)


        val userid = auth!!.currentUser?.uid
        val user = auth!!.currentUser


        db?.collection("links")?.document("app")?.get()
            ?.addOnSuccessListener {
                if (it.exists()){
                    link = it["link"].toString()
                }
            }
        db?.collection("links")?.document("privacy")?.get()
            ?.addOnSuccessListener {
                if (it.exists()){
                    links = it["web"].toString()
                }
            }

        db?.collection("links")?.document("story")?.get()
            ?.addOnSuccessListener {
                if (it.exists()){
                    story = it["web"].toString()
                }
            }
        db?.collection(ref)?.get()
            ?.addOnSuccessListener {
                for(x in it){
                    val name = x["name"].toString()
                    val branch = x["branch"].toString()
                    val year = x["year"].toString()
                    val grp:Boolean = x["AccusedByGroup"] as Boolean
                    val grpinfo = x["GroupInfo"].toString()
                    val desc = x["Desc"].toString()
                    val evidecne = x["Evidence"] as List<*>
                    val id = x.id

                    val r:reports = reports(name,branch,year,grp,grpinfo,desc,evidecne,id,ref)
                    reportList?.add(r)
                    adapter?.notifyDataSetChanged()
                }
            }

        db?.collection("colleges")!!.get()
            .addOnSuccessListener {
                var cc = emptyArray<String>()
                for(doc in it) {
                    cc = addElement(cc, doc.id)
                }
                for (x in cc){
                    db!!.collection(x).document(user?.email.toString()).get()
                        .addOnSuccessListener {
                            if (it.exists()){
                                val a:String = it["name"].toString()
                                profile.setText(a)
                            }
                        }
                }
            }


        if(!user!!.isEmailVerified){
            Toast.makeText(this, "email is not verified, Please Verify it", Toast.LENGTH_SHORT).show()
            super.onBackPressed()
        }
        else{

        }

        profile.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Logout")
            builder.setMessage("Wanna Logout??")
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

            builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                auth?.signOut()
                val i = Intent(this, MainActivity::class.java)
                startActivity(i)
                finish()
            }
            builder.setNegativeButton(android.R.string.no) { dialog, which ->
            }
            builder.show()
        }


    }

    override fun onResume() {
        super.onResume()
        adapter?.notifyDataSetChanged()
    }

//    override fun onResume() {
//        super.onResume()
//        finish()
//        startActivity(intent)
//    }

    override fun onBackPressed() {
        val drawerLayout:DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        else {
            super.onBackPressed()
        }
    }

    fun addElement(arr: Array<String>, element: String): Array<String> {
        val mutableArray = arr.toMutableList()
        mutableArray.add(element)
        return mutableArray.toTypedArray()
    }

    fun Snacker(s:String){
        val snack = Snackbar.make(drawerLayout!!,s, BaseTransientBottomBar.LENGTH_LONG)
        snack.view.setBackgroundColor(Color.parseColor("#4447BD"))
        snack.setTextColor(Color.parseColor("#FFFFFF"))
        snack.show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_info ->{
                val i = Intent(this, About::class.java)
                startActivity(i)
            }
            R.id.nav_privacy ->{
                val i = Intent(this, PrivacyPolicy::class.java)
                i.putExtra("link",links)
                startActivity(i)
            }
            R.id.nav_dev ->{
                val i = Intent(this, Developer::class.java)
                startActivity(i)
            }
            R.id.nav_share ->{
                shareit()
            }
            R.id.nav_story ->{
                val i = Intent(this,Developer::class.java)
                i.putExtra("story",story)
                startActivity(i)
            }
        }
        drawerLayout?.closeDrawer(GravityCompat.START)
        return true
    }

    fun shareit(){
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Guardain Protect")
        var shareMessage = "\nThe Anti Ragging App\n\n"
        shareMessage =
            """
            ${shareMessage}${link}
            
            
            """.trimIndent()
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        startActivity(Intent.createChooser(sharingIntent, "choose one"))
    }
}