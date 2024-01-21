package com.MounirKhalil.currencyexchange

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import com.MounirKhalil.currencyexchange.api.Authentication
import com.MounirKhalil.currencyexchange.api.ExchangeService
import com.MounirKhalil.currencyexchange.api.model.Token
import com.MounirKhalil.currencyexchange.api.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationActivity : AppCompatActivity() {
    private var usernameEditText: TextInputLayout? = null
    private var passwordEditText: TextInputLayout? = null
    private var submitButton: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        usernameEditText = findViewById(R.id.txtInptUsername)
        passwordEditText = findViewById(R.id.txtInptPassword)
        submitButton = findViewById(R.id.btnSubmit)
        submitButton?.setOnClickListener { view ->
            createUser()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(submitButton?.windowToken, 0)
        }
    }

    private fun createUser() {
        val user = User()
        user.username = usernameEditText?.editText?.text.toString()
        user.password = passwordEditText?.editText?.text.toString()
        ExchangeService.exchangeApi().addUser(user).enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                Snackbar.make(
                    submitButton as View, "Could not create account.", Snackbar.LENGTH_LONG
                ).show()
            }

            override fun onResponse(
                call: Call<User>, response: Response<User>
            ) {
                if (response.isSuccessful) {
                    ExchangeService.exchangeApi().authenticate(user)
                        .enqueue(object : Callback<Token> {
                            override fun onFailure(
                                call: Call<Token>, t: Throwable
                            ) {
                                return
                            }

                            override fun onResponse(
                                call: Call<Token>, response: Response<Token>
                            ) {
                                Snackbar.make(
                                    submitButton as View, "Account Created.", Snackbar.LENGTH_LONG
                                ).show()
                                response.body()?.token?.let {
                                    Authentication.saveToken(it)
                                }
                                submitButton?.postDelayed({
                                    onCompleted()
                                }, 1000)
                            }
                        })
                } else {
                    Snackbar.make(
                        submitButton as View, "Could not create account.", Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    private fun onCompleted() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}
