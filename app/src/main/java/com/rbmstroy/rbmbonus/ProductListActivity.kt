package com.rbmstroy.rbmbonus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.rbmstroy.rbmbonus.adapters.ProductListAdapter
import com.rbmstroy.rbmbonus.databinding.ActivityProductListBinding
import com.rbmstroy.rbmbonus.model.Event
import com.rbmstroy.rbmbonus.model.Product
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.product_card_unit.*
import org.json.JSONArray
import org.json.JSONObject

class ProductListActivity : AppCompatActivity() {
    lateinit var adapter: ProductListAdapter
    lateinit var binding: ActivityProductListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var recyclerView = binding.productPriceList
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ProductListAdapter()
        recyclerView.adapter = adapter

        getEvents()

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.headerFragment,HeaderApp())
            .commit()
    }

    fun stopActivity(view: View){
        finish()
    }

    private fun getEvents() {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url: String = "https://mobile.snabway.ru/api/controllers/productPrice.php"

        // Request a string response from the provided URL.
        val stringReq = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->

                var strResp = response.toString()
                val jsonObj: JSONObject = JSONObject(strResp)
                val jsonArray: JSONArray = jsonObj.getJSONArray("items")
                for (i in 0 until jsonArray.length()) {
                    var jsonInner: JSONObject = jsonArray.getJSONObject(i)
                    var price = "${jsonInner.getString("price")} Руб."
                    var product = Product(jsonInner.getString("img"),jsonInner.getString("name"),jsonInner.getString("brand"),price)
                    adapter.addProducts(product)
                }
            },
            Response.ErrorListener { Log.d("JsonLog","That didn't work!") })
        queue.add(stringReq)
    }
}