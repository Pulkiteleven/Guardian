package com.inertia.guardian.Activity

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.inertia.guardian.MainActivity
import com.inertia.guardian.NavActivity.About
import com.inertia.guardian.NavActivity.Developer
import com.inertia.guardian.NavActivity.OurStory
import com.inertia.guardian.NavActivity.PrivacyPolicy
import com.inertia.guardian.R
import java.io.File
import java.time.LocalDateTime


class Report : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    var auth: FirebaseAuth? = null

    //var db:FirebaseFirestore ?= null
    var fl: MutableList<Uri>? = null
    var aa: ArrayList<Int>? = null
    val item: MutableMap<String, Any> = HashMap()
    var total_evidence: TextView? = null
    var opener: Boolean = true
    var drawerLayout: DrawerLayout? = null

    var collegeName: String? = null

    val REQUEST_EXTERNAL_STORAGE = 1
    val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    val REQUEST_AUDIO = 2
    val PERMISSIONS_AUDIO = arrayOf(
        Manifest.permission.RECORD_AUDIO
    )

    var link: String? = null
    var links: String? = null
    var story: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.navigation_activity)

        supportActionBar?.hide()

        Permissions(this)
        newPermisiion(this)
        auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val nav: FloatingActionButton = findViewById(R.id.navigation)
        val profile: MaterialButton = findViewById(R.id.profile_btn)
        val accusedname: TextInputEditText = findViewById(R.id.accused_name)
        val accusedyear: AutoCompleteTextView = findViewById(R.id.accused_year)
        val accusedbranch: AutoCompleteTextView = findViewById(R.id.accused_Branch)
        val checked: MaterialCheckBox = findViewById(R.id.checked)
        val info: TextInputEditText = findViewById(R.id.note)
        val notev: TextInputLayout = findViewById(R.id.notev)
        val desc: TextInputEditText = findViewById(R.id.description)
        val evidence: MaterialButton = findViewById(R.id.evidence)
        val submit: MaterialButton = findViewById(R.id.submit_report)
        val protect: FloatingActionButton = findViewById(R.id.protect)
        total_evidence = findViewById(R.id.total_evidence)
        val progressBar = findViewById<ProgressBar>(R.id.progressbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        val navi: NavigationView = findViewById(R.id.nav_view)


        nav.setOnClickListener {
            drawerLayout?.openDrawer(GravityCompat.START)
        }

        navi.setNavigationItemSelectedListener(this)


        fl = ArrayList()


        db.collection("links").document("app").get()
            .addOnSuccessListener {
                if (it.exists()) {
                    link = it["link"].toString()
                }
            }

        db.collection("links").document("privacy").get()
            .addOnSuccessListener {
                if (it.exists()) {
                    links = it["web"].toString()
                }
            }

        db.collection("links").document("story").get()
            .addOnSuccessListener {
                if (it.exists()) {
                    story = it["web"].toString()
                }
            }
        db.collection("branch").get()
            .addOnSuccessListener {
                var cc = emptyArray<String>()
                for (doc in it) {
                    cc = addElement(cc, doc.id)
                }
                val adapter: ArrayAdapter<String> =
                    ArrayAdapter(this, R.layout.dropdown_menu_popup_item, cc)
                accusedbranch.setAdapter(adapter)
            }.addOnFailureListener {
                Toast.makeText(this, "failure", Toast.LENGTH_SHORT).show()
            }

        db.collection("year").get()
            .addOnSuccessListener {
                var cc = emptyArray<String>()
                for (doc in it) {
                    cc = addElement(cc, doc.id)
                }
                val adapter: ArrayAdapter<String> =
                    ArrayAdapter(this, R.layout.dropdown_menu_popup_item, cc)
                accusedyear.setAdapter(adapter)
            }.addOnFailureListener {
                Toast.makeText(this, "failure", Toast.LENGTH_SHORT).show()
            }

        val userid = auth!!.currentUser?.uid
        val user = auth!!.currentUser

        db.collection("colleges").get()
            .addOnSuccessListener {
                var cc = emptyArray<String>()
                for (doc in it) {
                    cc = addElement(cc, doc.id)
                }
                for (x in cc) {
                    db.collection(x).document(user?.email.toString()).get()
                        .addOnSuccessListener {
                            if (it.exists()) {
                                val a: String = it["name"].toString()
                                collegeName = it["college"].toString()
                                profile.setText(a)
                            }
                        }
                }
            }


        if (!user!!.isEmailVerified) {
            Toast.makeText(this, "email is not verified, Please Verify it", Toast.LENGTH_SHORT)
                .show()
            super.onBackPressed()
        } else {

        }




        evidence.setOnClickListener {
            var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
            chooseFile.setType("*/*")
            chooseFile.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            chooseFile = Intent.createChooser(chooseFile, "Choose a file")
            startActivityForResult(chooseFile, 1)
        }


        //Functionalities
        checked.setOnClickListener {
            if (checked.isChecked) {
                notev.visibility = View.VISIBLE
            } else {
                notev.visibility = View.GONE
            }
        }

        protect.setOnClickListener {
            val i = Intent(this, Protect_activity::class.java)
            startActivity(i)

        }


        submit.setOnClickListener {
            var s: String? = null
            val urls: MutableList<String>? = null
            val curent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDateTime.now()
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            val doc = curent.toString() + profile.text
            if (fl!!.size == 0) {
                Snacker("Evidence are Required")
            }
            if (TextUtils.isEmpty(accusedname.text.toString()) || TextUtils.isEmpty(accusedbranch.text.toString()) || TextUtils.isEmpty(
                    accusedyear.text.toString()
                ) || TextUtils.isEmpty(desc.text.toString())
            ) {
//                Toast.makeText(this, "The Given Fields Cannot be Empty", Toast.LENGTH_SHORT).show()
                Snacker("The Given Fields Cannot be Empty")
            } else {
                progressBar.visibility = View.VISIBLE
                var counter = 0
                for (i in 0..fl!!.size - 1) {
                    val Perfile: Uri = fl!!.get(i)

                    val folder: StorageReference =
                        FirebaseStorage.getInstance().getReference().child("Evidence")
                    val filename: StorageReference = folder.child("file" + Perfile.lastPathSegment)
                    val extension = "." + extension(fl!!.get(i))



                    filename.putFile(Perfile).addOnSuccessListener {
                        filename.downloadUrl.addOnSuccessListener {

                            var url = it.toString()
                            s += "," + url + extension

                            counter += 1
                            if (counter == fl!!.size) {
                                item.put("name", accusedname.text.toString())
                                item.put("branch", accusedbranch.text.toString())
                                item.put("year", accusedyear.text.toString())
                                item.put("AccusedByGroup", checked.isChecked)
                                item.put("GroupInfo", info.text.toString())
                                item.put("Desc", desc.text.toString())
                                val urlll = s?.split(",")
                                item.put("Evidence", s!!.split(","))

                                db.collection("report " + collegeName).document(doc)
                                    .set(item, SetOptions.merge())
                                    .addOnSuccessListener {
                                        progressBar.visibility = View.GONE
//                                            Snacker("Report Submitted")


                                        val builder = AlertDialog.Builder(this)
                                        builder.setTitle("Report")
                                        builder.setMessage("Report Submitted Successfully")


                                        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                                            accusedname.setText("")
                                            info.setText("")
                                            desc.setText("")
                                            fl = ArrayList()
                                            total_evidence?.text = "Choose Evidence"
                                        }

                                        builder.setNegativeButton(android.R.string.no) { dialog, which ->

                                        }
                                        builder.show()
                                    }
                            }
                        }
                    }


                }
            }

//            item.put("name",accusedname.text.toString())
//            item.put("branch",accusedbranch.text.toString())
//            item.put("year",accusedyear.text.toString())
//            item.put("AccusedByGroup",checked.isChecked)
//            item.put("GroupInfo",info.text.toString())
//            item.put("Desc",desc.text.toString())
//            item.put("Evidence",s!!.split(","))
//
//            db!!.collection("report").document()
//                .set(item)
//                .addOnSuccessListener {
//                    Toast.makeText(this, "Report Submitted Succesfully", Toast.LENGTH_SHORT).show()
//                }
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

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // for multiple files
                if (data?.clipData != null) {
                    val count: Int = data.clipData!!.itemCount
                    total_evidence?.text = count.toString() + " Evidences"
                    for (x in 0..count - 1) {
                        val File: Uri = data.clipData?.getItemAt(x)!!.uri
                        fl?.add(File)
                    }
                }
                // for single file
                else if (data?.data != null) {
                    val count: Int = 1
                    total_evidence?.text = count.toString() + " Evidence"
                    val uri: Uri = data.data!!
//
                    fl?.add(uri)
                }
            }
        }

    }


    fun extension(uri: Uri): String? {
        var ext: String? = null
        if (uri.scheme.equals(ContentResolver.SCHEME_CONTENT)) {
            val mime: MimeTypeMap = MimeTypeMap.getSingleton()
            ext = mime.getExtensionFromMimeType(contentResolver.getType(uri))
        } else {
            ext = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path!!)).toString())

        }
        return ext
    }

    fun addElement(arr: Array<String>, element: String): Array<String> {
        val mutableArray = arr.toMutableList()
        mutableArray.add(element)
        return mutableArray.toTypedArray()
    }


    fun Permissions(activity: Activity) {
        val permission =
            ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)


        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }

    }

    fun newPermisiion(activity: Activity) {
        val permission2 =
            ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
        if (permission2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_AUDIO,
                REQUEST_AUDIO
            )
        }
    }

    fun Snacker(s: String) {
        val snack = Snackbar.make(drawerLayout!!, s, LENGTH_LONG)
        snack.view.setBackgroundColor(Color.parseColor("#4447BD"))
        snack.setTextColor(Color.parseColor("#FFFFFF"))
        snack.show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_info -> {
                val i = Intent(this, About::class.java)
                startActivity(i)
            }
            R.id.nav_privacy -> {
                val i = Intent(this, PrivacyPolicy::class.java)
                i.putExtra("link", links)
                startActivity(i)
            }
            R.id.nav_dev -> {
                val i = Intent(this, Developer::class.java)
                startActivity(i)
            }
            R.id.nav_share -> {
                shareit()
            }
            R.id.nav_story -> {
                val i = Intent(this, OurStory::class.java)
                i.putExtra("story", story)
                startActivity(i)
            }
        }
        drawerLayout?.closeDrawer(GravityCompat.START)
        return true
    }

    fun shareit() {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Guardain Protect")
        var shareMessage = "\nThe Anti Ragging App\n\n"
        shareMessage =
            """ ${shareMessage}${link}
            """.trimIndent()
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        startActivity(Intent.createChooser(sharingIntent, "choose one"))
    }

}