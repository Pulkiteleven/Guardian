package com.inertia.guardian.Activity

import android.R.attr
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.inertia.guardian.MainActivity
import com.inertia.guardian.R


class Signin : AppCompatActivity() {

    var auth:FirebaseAuth ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        val db = FirebaseFirestore.getInstance()
        val email:TextInputEditText = findViewById(R.id.email)
        val password:TextInputEditText = findViewById(R.id.password)
        val forgetPassword:TextView = findViewById(R.id.forgetpassword)

        val login:Button = findViewById(R.id.login)
        val signup:Button = findViewById(R.id.signup)


//        if(!user!!.isEmailVerified){
//            Toast.makeText(this, "email is not verified", Toast.LENGTH_SHORT).show()
//        }
//        else{
//            Toast.makeText(this, "email is verified", Toast.LENGTH_SHORT).show()
//        }

        login.setOnClickListener {
            if (TextUtils.isEmpty(email.text.toString())){
               Snacker("Email cannot be empty")
            }
            else if(TextUtils.isEmpty(password.text.toString())){
               Snacker("Password cannot be empty")
            }
            else {
                val user = auth!!.currentUser

                    auth!!.signInWithEmailAndPassword(
                        email.text.toString(),
                        password.text.toString()
                    )
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                var isAdmin:Boolean = false
                                var collegename:String ?= null
                                db.collection("admins").get()
                                    .addOnSuccessListener {
                                        for(x in it){
                                            if (email.text.toString().equals(x["name"].toString())){
                                                isAdmin = true
                                                collegename = x["college"].toString()
                                                break
                                            }
                                        }
                                        if (isAdmin){
                                            val i = Intent(this@Signin,AdminPanel::class.java)
                                            i.putExtra("college",collegename)
                                            startActivity(i)
                                            finish()
                                        }
                                        else {
                                            startActivity(Intent(this@Signin, Report::class.java))
                                            finish()
                                        }
                                    }

                            } else {
                                Snacker("Log in Error: ")
                            }

                }
            }
        }

        signup.setOnClickListener {
            val i = Intent(Signin@this,SignUp::class.java)
            startActivity(i)
            finish()
        }

        forgetPassword.setOnClickListener {
            if (!TextUtils.isEmpty(email.text.toString())) {
                auth?.fetchSignInMethodsForEmail(email.text.toString())
                    ?.addOnCompleteListener {
                        val bool: Boolean = it.getResult()!!.getSignInMethods()!!.isEmpty()
                        if (!bool) {
                            val builder = AlertDialog.Builder(this)
                            builder.setTitle("Reset Password")
                            builder.setMessage("Do you really want to reset password")
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

                            builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                                auth?.sendPasswordResetEmail(email.text.toString())
                                    ?.addOnSuccessListener {
                                        Snacker("Password Reset Link has been Sent")
                                    }
                            }

                            builder.setNegativeButton(android.R.string.no) { dialog, which ->

                            }


                            builder.show()
                        } else {
                            Snacker("Email is not registered")
                        }
                    }
            }
            else{
                Snacker("email cannot be empty")
            }
        }

    }

    fun Snacker(s:String){
        val root: RelativeLayout = findViewById(R.id.root)
        val snack = Snackbar.make(root,s, BaseTransientBottomBar.LENGTH_LONG)
        snack.view.setBackgroundColor(Color.parseColor("#4447BD"))
        snack.setTextColor(Color.parseColor("#FFFFFF"))
        snack.show()
    }
}