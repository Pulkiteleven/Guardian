package com.inertia.guardian.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.inertia.guardian.R
import com.inertia.guardian.adapters.contactadapter
import com.inertia.guardian.models.contacts


class ContactPicker : AppCompatActivity() {

    private val DISPLAY_NAME =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) ContactsContract.Contacts.DISPLAY_NAME_PRIMARY else ContactsContract.Contacts.DISPLAY_NAME

    private val FILTER: String = DISPLAY_NAME.toString() + " NOT LIKE '%@%'"

    private val ORDER = java.lang.String.format("%1\$s COLLATE NOCASE", DISPLAY_NAME)

    @SuppressLint("InlinedApi")
    private val PROJECTION = arrayOf(
        ContactsContract.Contacts._ID,
        DISPLAY_NAME,
        ContactsContract.Contacts.HAS_PHONE_NUMBER
    )

    var contacts:RecyclerView ?= null
    var contactList: MutableList<contacts> ?= null
    var adaper:contactadapter ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_picker)

        supportActionBar?.hide()

        contacts = findViewById(R.id.contacs_list)
        var procced:FloatingActionButton = findViewById(R.id.proceed)
        contacts!!.layoutManager = GridLayoutManager(this,1)
        contactList = ArrayList()

        adaper = contactadapter(this,contactList as ArrayList<contacts>)
        contacts!!.adapter = adaper
        ShowContacts()

        procced.setOnClickListener{
            val i = Intent(this,Protect_activity::class.java)
            startActivity(i)
            finish()
        }

    }
    
    

    @SuppressLint("Range")
    private fun ShowContacts(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS),
                1
            )
        }
            else{
            var cursor: Cursor? = this.contentResolver.query(ContactsContract.Contacts.CONTENT_URI,null,
                null,null,ContactsContract.Contacts.DISPLAY_NAME + " ASC")

            val cr:ContentResolver = contentResolver
            val c:Cursor ?= cr.query(
                ContactsContract.Contacts.CONTENT_URI,
                PROJECTION,FILTER,null,ORDER)

            if (c != null && c.moveToFirst()){
                do {
                    val id =
                        c!!.getString(c.getColumnIndex(ContactsContract.Contacts._ID))
                    val name = c.getString(c.getColumnIndex(DISPLAY_NAME))
                    val hasPhone =
                        c.getInt(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))

                    var phone: String = ""
                    if (hasPhone > 0) {
                        val cp = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            arrayOf(id),
                            null
                        )
                        if (cp != null && cp.moveToFirst()) {
                            phone =
                                cp.getString(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            cp.close()
                        }
                    }
                    if (!TextUtils.isEmpty(name) || !TextUtils.isEmpty(phone)){
                        val c: contacts = contacts(name,phone)
                        contactList?.add(c)
                        adaper!!.notifyDataSetChanged()
                    }
                }
                    while (c.moveToNext())
            }


//            cursor?.moveToFirst()
//
//            while (cursor!!.moveToNext()) {
//                var names =
//                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
//                    var noo =
//                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER))
//                            .toString()
//
//                    if (noo.equals("1")) {
//                        var no =
//                            cursor.getString(cursor.getColumnIndex())
//
//                        val c: contacts = contacts(names, no)
//                        contactList?.add(c)
//                        adaper!!.notifyDataSetChanged()
//                    }
//
//            }


            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                ShowContacts()
            }
            else{
                Toast.makeText(this, "You Didn't Grant Permission", Toast.LENGTH_SHORT).show()
            }
        }
    }
}




