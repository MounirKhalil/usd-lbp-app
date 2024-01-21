package com.MounirKhalil.currencyexchange

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import com.MounirKhalil.currencyexchange.api.ExchangeService
import com.MounirKhalil.currencyexchange.api.model.ExchangeRates
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ExchangeFragment : Fragment() {
    private lateinit var amountEditText: EditText
    private lateinit var currencySpinner: Spinner
    private lateinit var calculateButton: Button
    private lateinit var resultTextView: TextView
    private lateinit var buyUsdTextView: TextView
    private lateinit var sellUsdTextView: TextView

    private var lbpToUsdRate: Float? = null
    private var usdToLbpRate: Float? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchRates()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(
            R.layout.fragment_exchange, container, false
        )

        // here we initilized views
        amountEditText = view.findViewById(R.id.amountEditText)
        currencySpinner = view.findViewById(R.id.currencySpinner)
        calculateButton = view.findViewById(R.id.calculateButton)
        resultTextView = view.findViewById(R.id.resultTextView)
        buyUsdTextView = view.findViewById(R.id.txtBuyUsdRate)
        sellUsdTextView = view.findViewById(R.id.txtSellUsdRate)

        // setting up our spinner
        val currencyAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.currency_options,
            android.R.layout.simple_spinner_item
        )
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        currencySpinner.adapter = currencyAdapter

        // calculate button click listener
        calculateButton.setOnClickListener {
            calculateExchange()
        }

        fetchRates()
        return view
    }

    private fun fetchRates() {
        ExchangeService.exchangeApi().getExchangeRates().enqueue(object : Callback<ExchangeRates> {
            override fun onResponse(
                call: Call<ExchangeRates>, response: Response<ExchangeRates>
            ) {
                val responseBody: ExchangeRates? = response.body();
                lbpToUsdRate = responseBody?.lbpToUsd
                usdToLbpRate = responseBody?.usdToLbp
                lbpToUsdRate?.let { buyUsdTextView.text = String.format("%.2f LBP", it) }
                usdToLbpRate?.let { sellUsdTextView.text = String.format("%.2f LBP", it) }
            }

            override fun onFailure(call: Call<ExchangeRates>, t: Throwable) {
                return;
            }
        })
    }

    private fun calculateExchange() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
        val inputAmount = amountEditText.text.toString().toFloatOrNull()

        if (inputAmount != null && lbpToUsdRate != null && usdToLbpRate != null && lbpToUsdRate?.equals(
                0
            ) != true && usdToLbpRate?.equals(0) != true
        ) {
            val selectedCurrency = currencySpinner.selectedItem.toString()
            val exchangedAmount = when (selectedCurrency) {
                "I have ... USD, how much LBP can I get?" -> inputAmount * usdToLbpRate!!
                "I want ... USD, how much LBP should I have?" -> inputAmount * lbpToUsdRate!!
                "I have ... LBP, how much USD can I get?" -> inputAmount / lbpToUsdRate!!
                "I want ... LBP, how much USD should I have?" -> inputAmount / usdToLbpRate!!
                else -> 0f
            }
            resultTextView.text = exchangedAmount.toString()
        } else {
            resultTextView.text = "Invalid input or rates not available"
        }
    }
}
