package com.rbmstroy.rbmbonus.tabs

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
import com.rbmstroy.rbmbonus.R
import com.rbmstroy.rbmbonus.adapters.EventAdapter
import com.rbmstroy.rbmbonus.model.Event
import com.rbmstroy.rbmbonus.tools.SharedPreference
import org.json.JSONArray
import org.json.JSONObject

class EventTab : Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: EventAdapter
    var token = ""

    private val imageIdList = listOf(R.drawable.qr_scan,R.drawable.auchan,R.drawable.sportmaster,R.drawable.letual,R.drawable.mvideo,R.drawable.eldorado)
    private val titleList = listOf("QR Code","Ашан","Спортмастер","Л'Этуаль","МВидео","Эльдорадо")
    private val eventTextList = listOf("Сканирование кода 12.12.2021","Подарочная карта 10.08.2021","Подарочная карта 10.08.2021","Подарочная карта 10.08.2021","Подарочная карта 10.08.2021","Подарочная карта 10.08.2021")
    private val eventPriceList = listOf("+50₽","-500₽","-500₽","-500₽","-500₽","-500₽")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var viewEvent =  inflater.inflate(R.layout.fragment_event_tab, container, false)
        recyclerView = viewEvent.findViewById(R.id.eventsRv)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = EventAdapter()
        recyclerView.adapter = adapter

        val sharedPreference: SharedPreference = SharedPreference(requireContext())
        token = sharedPreference.getValueString("token").toString()
        getEvents(token,imageIdList)

        return viewEvent
    }


    override fun onResume() {
        super.onResume()

        adapter.deleteItem()

        val sharedPreference: SharedPreference = SharedPreference(requireContext())
        token = sharedPreference.getValueString("token").toString()
        getEvents(token,imageIdList)

        Log.d("Activity", "Started!")

    }

    private fun getEvents(token: String, imageIdList : List<Int>) {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(context)
        val url: String = "https://mobile.snabway.ru/api/controllers/EventsController.php/?token=$token"

        // Request a string response from the provided URL.
        val stringReq = StringRequest(Request.Method.GET, url,
            Response.Listener<String> { response ->

                var strResp = response.toString()
                val jsonObj: JSONObject = JSONObject(strResp)
                val jsonArray: JSONArray = jsonObj.getJSONArray("items")
                for (i in 0 until jsonArray.length()) {
                    var jsonInner: JSONObject = jsonArray.getJSONObject(i)
                    var event = Event(imageIdList[jsonInner.getString("imgIndex").toInt()],jsonInner.getString("name"),jsonInner.getString("dataEvent"),jsonInner.getString("balance"))
                    adapter.addEvent(event)
                }
            },
            Response.ErrorListener { Log.d("JsonLog","That didn't work!") })
        queue.add(stringReq)
    }

}