package com.MounirKhalil.currencyexchange

import android.util.Log
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.MounirKhalil.currencyexchange.api.Authentication
import com.MounirKhalil.currencyexchange.api.ExchangeService
import com.MounirKhalil.currencyexchange.api.model.Transaction
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionsFragment : Fragment() {
    private var listview: ListView? = null
    private var transactions: ArrayList<Transaction>? = ArrayList()
    private var adapter: TransactionAdapter? = null

    class TransactionAdapter(
        private val inflater: LayoutInflater, private val dataSource: List<Transaction>
    ) : BaseAdapter() {
        override fun getView(
            position: Int, convertView: View?, parent: ViewGroup?
        ): View {
            val view: View = inflater.inflate(
                R.layout.item_transaction, parent, false
            )

            val transactionSpace = view.findViewById<TextView>(R.id.transaction_space)
            val addedDate = view.findViewById<TextView>(R.id.added_date)

            val currentTransaction = dataSource[position]

            transactionSpace.text =
                if (currentTransaction.usdToLbp == true)
                    "${currentTransaction.usdAmount} USD to ${currentTransaction.lbpAmount} LBP"
                else
                    "${currentTransaction.lbpAmount} LBP to ${currentTransaction.usdAmount} USD"


            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = dateFormat.parse(currentTransaction.addedDate)
            val displayDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            addedDate.text = "Date: ${displayDateFormat.format(date)}"
            return view
        }

        override fun getItem(position: Int): Any {
            return dataSource[position]
        }

        override fun getItemId(position: Int): Long {
            return dataSource[position].id?.toLong() ?: 0
        }

        override fun getCount(): Int {
            return dataSource.size
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchTransactions()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(
            R.layout.fragment_transactions, container, false
        )
        listview = view.findViewById(R.id.listview)
        adapter = TransactionAdapter(layoutInflater, transactions!!)
        listview?.adapter = adapter
        return view
    }

    private fun fetchTransactions() {
        if (Authentication.getToken() != null) {
            ExchangeService.exchangeApi().getTransactions("Bearer ${Authentication.getToken()}")
                .enqueue(object : Callback<List<Transaction>> {
                    override fun onFailure(
                        call: Call<List<Transaction>>, t: Throwable
                    ) {
                        Log.d(
                            "TransactionsFragment", "fetchTransactions onFailure: ${t.message}"
                        )
                        return
                    }

                    override fun onResponse(
                        call: Call<List<Transaction>>, response: Response<List<Transaction>>
                    ) {
                        if (response.isSuccessful) {
                            transactions?.addAll(response.body()!!)
                            adapter?.notifyDataSetChanged()
                            Log.d(
                                "TransactionsFragment",
                                "fetchTransactions onResponse: ${response.body()}"
                            )
                        } else {
                            Log.d(
                                "TransactionsFragment",
                                "fetchTransactions onResponse: Error, response code: ${response.code()}"
                            )
                        }
                    }
                })
        } else {
            Log.d("TransactionsFragment", "fetchTransactions: User not logged in")
        }
    }


}