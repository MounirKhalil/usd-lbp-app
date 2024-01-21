package com.MounirKhalil.currencyexchange

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.MounirKhalil.currencyexchange.api.Authentication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.MounirKhalil.currencyexchange.api.ExchangeService
import com.MounirKhalil.currencyexchange.api.model.ExchangeRates
import com.MounirKhalil.currencyexchange.api.model.Transaction
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textfield.TextInputLayout


class MainActivity : AppCompatActivity() {

    private var buyUsdTextView: TextView? = null
    private var sellUsdTextView: TextView? = null
    private var fab: FloatingActionButton? = null
    private var transactionDialog: View? = null
    private var menu: Menu? = null
    private var tabLayout: TabLayout? = null
    private var tabsViewPager: ViewPager2? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Authentication.initialize(this)
        setContentView(R.layout.activity_main)
        fab = findViewById(R.id.fab)
        fab?.setOnClickListener { view ->
            showDialog()
        }
        tabLayout = findViewById(R.id.tabLayout)
        tabsViewPager = findViewById(R.id.tabsViewPager)
        tabLayout?.tabMode = TabLayout.MODE_FIXED
        tabLayout?.isInlineLabel = true
        // Enable Swipe
        tabsViewPager?.isUserInputEnabled = true
        // Set the ViewPager Adapter
        val adapter = TabsPagerAdapter(supportFragmentManager, lifecycle)
        tabsViewPager?.adapter = adapter
        TabLayoutMediator(tabLayout!!, tabsViewPager!!) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Exchange"
                }
                1 -> {
                    tab.text = "Transactions"
                }
            }
        }.attach()

    }

    private fun showDialog() {
        transactionDialog = LayoutInflater.from(this)
            .inflate(R.layout.dialog_transaction, null, false)
        MaterialAlertDialogBuilder(this).setView(transactionDialog)
            .setTitle("Add Transaction")
            .setMessage("Enter transaction details")
            .setPositiveButton("Add") { dialog, _ ->
                var usdAmount = "0".toFloat()
                val usdAmountS =
                    transactionDialog?.findViewById<TextInputLayout>(R.id.txtInptUsdAmount)?.editText?.text.toString()
                if (usdAmountS != "") usdAmount = usdAmountS.toFloat()
                var lbpAmount = "0".toFloat()
                val lbpAmountS =
                    transactionDialog?.findViewById<TextInputLayout>(R.id.txtInptLbpAmount)?.editText?.text.toString()
                if (lbpAmountS != "") lbpAmount = lbpAmountS.toFloat()
                val buyButtonIsChecked =
                    transactionDialog?.findViewById<RadioButton>(R.id.rdBtnBuyUsd)?.isChecked
                        ?: false
                val sellButtonIsChecked =
                    transactionDialog?.findViewById<RadioButton>(R.id.rdBtnSellUsd)?.isChecked
                        ?: false
                if (usdAmount <= 0 || lbpAmount <= 0 || !buyButtonIsChecked && !sellButtonIsChecked) {
                    Toast.makeText(
                        this@MainActivity,
                        "Transaction Failure: Missing Info",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val usdToLbp: Boolean? = sellButtonIsChecked
                    val t = Transaction()
                    t.usdAmount = usdAmount
                    t.lbpAmount = lbpAmount
                    t.usdToLbp = usdToLbp
                    addTransaction(t)

                }
                dialog.dismiss()
            }

            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    private fun addTransaction(transaction: Transaction) {

        ExchangeService.exchangeApi().addTransaction(
            transaction,
            if (Authentication.getToken() != null) "Bearer ${Authentication.getToken()}" else null
        ).enqueue(object :
            Callback<Any> {
            override fun onResponse(
                call: Call<Any>, response:
                Response<Any>
            ) {
                Snackbar.make(
                    fab as View, "Transaction added!",
                    Snackbar.LENGTH_LONG
                )
                    .show()
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Snackbar.make(
                    fab as View, "Could not add transaction.",
                    Snackbar.LENGTH_LONG
                )
                    .show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menu = menu
        setMenu()
        return true
    }

    private fun setMenu() {
        menu?.clear()
        menuInflater.inflate(
            if (Authentication.getToken() == null)
                R.menu.menu_logged_out else R.menu.menu_logged_in, menu
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.login) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        } else if (item.itemId == R.id.register) {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        } else if (item.itemId == R.id.logout) {
            Authentication.clearToken()
            setMenu()
        }
        return true
    }


}