package com.inertia.guardian.Activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.inertia.guardian.R


class SignUp : AppCompatActivity() {

    var auth:FirebaseAuth ?= null
    val item: MutableMap<String,Any> = HashMap()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        supportActionBar?.hide()
        val db = FirebaseFirestore.getInstance()
        val email:TextInputEditText = findViewById(R.id.email)
        val name:TextInputEditText = findViewById(R.id.name)
        val password:TextInputEditText = findViewById(R.id.password)
        val confpassword:TextInputEditText = findViewById(R.id.confirm_password)
        val city:TextInputEditText = findViewById(R.id.city)
        val college:AutoCompleteTextView = findViewById(R.id.college)
        val year:AutoCompleteTextView = findViewById(R.id.year)
        val branch:AutoCompleteTextView = findViewById(R.id.branch)

        val signUp:Button = findViewById(R.id.signup)
        val signin:Button = findViewById(R.id.login)

        auth = FirebaseAuth.getInstance()

        db.collection("colleges").get()
            .addOnSuccessListener {
                var cc = emptyArray<String>()
                for(doc in it){
                    cc = addElement(cc,doc.id)
                }
                val adapter:ArrayAdapter<String> = ArrayAdapter(this,R.layout.dropdown_menu_popup_item,cc)
                college.setAdapter(adapter)
            }.addOnFailureListener {
                Snacker("Failure")
            }
        db.collection("branch").get()
            .addOnSuccessListener {
                var cc = emptyArray<String>()
                for(doc in it){
                    cc = addElement(cc,doc.id)

                }
                val adapter:ArrayAdapter<String> = ArrayAdapter(this,R.layout.dropdown_menu_popup_item,cc)
                branch.setAdapter(adapter)
            }.addOnFailureListener {
                Snacker( "failure")
            }
        db.collection("year").get()
            .addOnSuccessListener {
                var cc = emptyArray<String>()
                for(doc in it){
                    cc = addElement(cc,doc.id)

                }
                val adapter:ArrayAdapter<String> = ArrayAdapter(this,R.layout.dropdown_menu_popup_item,cc)
                year.setAdapter(adapter)
            }.addOnFailureListener {
                Snacker( "failure")
            }




        signUp.setOnClickListener {
//            val item: MutableMap<String,Any> = HashMap()
            item.put("name",name.text.toString())
            item.put("email",email.text.toString())
            item.put("city",city.text.toString())
            item.put("college",college.text.toString())
            item.put("year",year.text.toString())
            item.put("branch",branch.text.toString())

            if(!TextUtils.isEmpty(email.text.toString())) {
                auth?.fetchSignInMethodsForEmail(email.text.toString())
                    ?.addOnCompleteListener {
                        val bool: Boolean = it.getResult()!!.getSignInMethods()!!.isEmpty()
                        if (bool) {
                            createUser(
                                email.text.toString(),
                                password.text.toString(),
                                confpassword.text.toString(),
                                city.text.toString(),
                                branch.text.toString(),
                                year.text.toString(),
                                college.text.toString()
                            )
                        } else {
                            Snacker("Email is Already Registered")
                        }
                    }?.addOnFailureListener {
                        Snacker("Error while verifying",)
                    }
            }

        }

        signin.setOnClickListener {
            val i = Intent(this,signin::class.java)
            startActivity(i)
            finish()
        }

    }

    private fun createUser(email:String,password:String,cpassword:String,city:String,branch:String,year:String,college:String) {




        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(city) || TextUtils.isEmpty(branch) || TextUtils.isEmpty(year) || TextUtils.isEmpty(college)){
            Snacker("Given Fields Cannot be Empty")
        }
        else if(!password.equals(cpassword)){
            Snacker("Both Passwords Are not same")
        }
        else if(password.length < 6){
            Snacker("Password Length Must be more than 6")
        }
        else {
            val db = FirebaseFirestore.getInstance()
            db.collection(college).document(email)
                .set(item)
                .addOnSuccessListener {
                    Snacker("Data Fetched")
                }.addOnFailureListener {
                    Snacker("Error")
                }
                auth?.createUserWithEmailAndPassword(email, password)!!.addOnCompleteListener(
                    OnCompleteListener<AuthResult?> { task ->
                        if (task.isSuccessful) {
                            //sent verification link
                            val user = auth!!.currentUser
                            user?.sendEmailVerification()?.addOnSuccessListener {
                                Toast.makeText(this, "Verification Mail has been sent", Toast.LENGTH_SHORT).show()
                            }?.addOnFailureListener {
                                Snacker("Error Sending Mail")
                            }
                            startActivity(Intent(this@SignUp, Signin::class.java))
                            finish()
                        } else {
                            Snacker("Registration Error: " + task.exception?.message)

                        }
                    })


        }
    }

    fun addElement(arr: Array<String>, element: String): Array<String> {
        val mutableArray = arr.toMutableList()
        mutableArray.add(element)
        return mutableArray.toTypedArray()
    }

    fun Snacker(s:String){
        val root:RelativeLayout = findViewById(R.id.root)
        val snack = Snackbar.make(root,s, BaseTransientBottomBar.LENGTH_LONG)
        snack.view.setBackgroundColor(Color.parseColor("#4447BD"))
        snack.setTextColor(Color.parseColor("#FFFFFF"))
        snack.show()
    }


}
