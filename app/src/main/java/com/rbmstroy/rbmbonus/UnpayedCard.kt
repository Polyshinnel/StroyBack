package com.rbmstroy.rbmbonus

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.rbmstroy.rbmbonus.adapters.UnpayedCardAdapter
import com.rbmstroy.rbmbonus.interfaces.UnpayedCardInterface
import com.rbmstroy.rbmbonus.model.UnpayedCard
import com.rbmstroy.rbmbonus.tools.SharedPreference
import org.json.JSONArray
import org.json.JSONObject


class UnpayedCard : Fragment() {

    private lateinit var recyclerView: RecyclerView
    lateinit var adapter: UnpayedCardAdapter
    private val titleList = listOf("Ашан","Спортмастер","Лэтуаль","М.Видео","Эльдорадо")
    private val awaitSum = listOf("500 р","1000 р","2000 р","500 р","1000 р")
    private var counter = 0
    var token = ""
    private val imageIdList = listOf(R.drawable.qr_scan,R.drawable.auchan,R.drawable.sportmaster,R.drawable.letual,R.drawable.mvideo,R.drawable.eldorado)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var viewUnpayed = inflater.inflate(R.layout.fragment_unpayed_card, container, false)
        recyclerView = viewUnpayed.findViewById(R.id.unpayedRv)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = UnpayedCardAdapter(object : UnpayedCardInterface{
            override fun onClicked(UnPayedCard: UnpayedCard) {
                Log.d("MyLog","${UnPayedCard.ImgId}, ${UnPayedCard.UnPayedSum}, ${UnPayedCard.UnPayedTitle}")
                val intent = Intent(context,AceptCard::class.java)

                val imgId = UnPayedCard.ImgId.toString().toInt()
                intent.putExtra("imgId", imgId)
                intent.putExtra("title", "${UnPayedCard.UnPayedTitle}")
                intent.putExtra("balance", "${UnPayedCard.UnPayedSum}")

                startActivity(intent)
            }

        })

        recyclerView.adapter = adapter

        val sharedPreference: SharedPreference = SharedPreference(requireContext())
        token = sharedPreference.getValueString("token").toString()


        return viewUnpayed
    }

    override fun onResume() {
        super.onResume()

        adapter.deleteCard()

        val sharedPreference: SharedPreference = SharedPreference(requireContext())
        token = sharedPreference.getValueString("token").toString()
        getUnpayedCards(token,imageIdList)
    }

    private fun getUnpayedCards(token: String, imageIdList : List<Int>) {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(context)
        val url: String = "https://mobile.snabway.ru/api/controllers/getCardRequements.php/?token=$token"

        // Request a string response from the provided URL.
        val stringReq = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->

                var strResp = response.toString()
                val jsonObj: JSONObject = JSONObject(strResp)
                val jsonArray: JSONArray = jsonObj.getJSONArray("items")
                for (i in 0 until jsonArray.length()) {
                    var jsonInner: JSONObject = jsonArray.getJSONObject(i)
                    var unpayedCard = UnpayedCard(imageIdList[jsonInner.getString("imgId").toInt()],jsonInner.getString("cardName"),jsonInner.getString("balance"))
                    adapter.addCard(unpayedCard)
                }
            },
            Response.ErrorListener { Log.d("JsonLog","That didn't work!") })
        queue.add(stringReq)
    }

}