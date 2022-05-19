package com.rbmstroy.rbmbonus

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.rbmstroy.rbmbonus.tools.SharedPreference
import org.json.JSONObject


class HeaderApp : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sharedPreference: SharedPreference = SharedPreference(requireContext())
        val token = sharedPreference.getValueString("token").toString()

        val viewHeader = inflater.inflate(R.layout.fragment_header_app, container, false)

        getUserInfo(token, viewHeader)

        val supportText = viewHeader.findViewById<TextView>(R.id.supportLink)
        supportText.setOnClickListener {
            startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:+79036365857")))
        }

        // Inflate the layout for this fragment
        return viewHeader
    }

    private fun getCallPermission(){
        if(ActivityCompat.checkSelfPermission(requireContext(),android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(context, "Требуется разрешение на звонки!", Toast.LENGTH_SHORT).show()
        }else{
            startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:+79036365857")))
        }
    }

    private fun getUserInfo(token: String, viewHeader: View) {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(requireContext())
        val url: String = "https://mobile.snabway.ru/api/controllers/UserController.php/?token=$token"

        // Request a string response from the provided URL.
        val stringReq = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->

                var strResp = response.toString()
                val jsonObj: JSONObject = JSONObject(strResp)
                val balance = jsonObj.getString("balance")
                val name = jsonObj.getString("username")

                viewHeader.findViewById<TextView>(R.id.headerTitle).text = name
            },
            Response.ErrorListener { Log.d("JsonLog","That didn't work!") })
        queue.add(stringReq)
    }

}