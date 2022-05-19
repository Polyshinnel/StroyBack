package com.rbmstroy.rbmbonus

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.rbmstroy.rbmbonus.databinding.ActivitySuccessCardBinding
import com.rbmstroy.rbmbonus.tools.SharedPreference
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.squareup.picasso.Picasso
import org.json.JSONObject


class SuccessCard : AppCompatActivity() {
    private lateinit var binding:ActivitySuccessCardBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    var token = ""
    var volleyRequestQueue: RequestQueue? = null
    var dialog: ProgressDialog? = null
    val serverAPIURL: String = "https://mobile.snabway.ru/api/controllers/ScanQrCode.php"
    val TAG = "Response tag"
    var geo = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuccessCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.headerHolderSucess, HeaderApp())
            .commit()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fetchLocation()

        val sharedPreference: SharedPreference = SharedPreference(this)
        token = sharedPreference.getValueString("token").toString()
        geo = sharedPreference.getValueString("geo").toString()

        val qrLink = intent.getStringExtra("qrCode").toString()
        var demiter = "?"
        var arrayString = qrLink.split(demiter)
        Log.d("ArrUnit","${arrayString[0]}")
        Log.d("QR-text",qrLink)

        if(arrayString[0] == "https://mobile.snabway.ru/qr-code.php/"){
            scanQr(token,qrLink,geo)
        }else{
            binding.prodSucces.visibility = View.GONE
            binding.prodImg.setImageResource(R.drawable.ic_error_scan)
            binding.noticeProd.text = "Ошибка распознавания QR кода!"
            binding.noticeText.text = "Попробуйте пожалуйста снова."
        }

    }


    private fun fetchLocation() {
        val task = fusedLocationProviderClient.lastLocation

        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION), 101)
            return
        }

        task.addOnSuccessListener {
            if(it != null){
                Log.d("MyLog1","${it.latitude} ${it.longitude}")

                val geo = "${it.latitude},${it.longitude}"
                val sharedPreference: SharedPreference = SharedPreference(this)
                sharedPreference.save("geo",geo)
            }else{
                Log.d("MyLog","Не получается определить текущие координаты")
            }
        }
    }

    fun endActivity(view: View){
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun scanQr(token: String, qrLink: String, geo: String) {
        volleyRequestQueue = Volley.newRequestQueue(this)
        dialog = ProgressDialog.show(this, "", "Отправка данных ...", true);
        val parameters: MutableMap<String, String> = HashMap()
        // Add your parameters in HashMap
        parameters.put("token",token);
        parameters.put("qr_code",qrLink);
        parameters.put("geo", geo)

        val strReq: StringRequest = object : StringRequest(
            Method.POST,serverAPIURL,
            Response.Listener { response ->
                Log.e(TAG, "response: " + response)
                dialog?.dismiss()

                // Handle Server response here
                try {
                    val responseObj = JSONObject(response)

                    val message = responseObj.getString("message")
                    if (message == "scan ok") {
                        val imgUrl = responseObj.getString("imgUrl")
                        Picasso.get().load(imgUrl).into(binding.prodImg)
                        binding.noticeProd.text = responseObj.getString("prodName")
                        val price = responseObj.getString("balance")
                        binding.noticeText.text = "Ваш бонусный баланс пополнен на $price ₽ "
                    }

                    if (message == "scan err") {
                        binding.prodSucces.visibility = View.GONE
                        binding.prodImg.setImageResource(R.drawable.ic_error_scan)
                        binding.noticeProd.text = "Ошибка! Данный QR код уже зарегистрирован в нашей системе!"
                        binding.noticeText.text = "Напоминаем что сканирование QR кодов возможно только единожды!"
                    }

                    if (message == "recognize err") {
                        binding.prodSucces.visibility = View.GONE
                        binding.prodImg.setImageResource(R.drawable.ic_error_scan)
                        binding.noticeProd.text = "Ошибка распознавания QR кода!"
                        binding.noticeText.text = "Попробуйте пожалуйста снова."
                    }

                    if (message == "geo err") {
                        binding.prodSucces.visibility = View.GONE
                        binding.prodImg.setImageResource(R.drawable.ic_error_scan)
                        binding.noticeProd.text = "Ошибка! Судя по всему вы находитесь не на точке продаж!"
                        binding.noticeText.text = "Напоминаем что сканирование товаров возможно только на точке продаж указанной при регистрации "
                    }

                    Log.d("TestJson","$response")

                } catch (e: Exception) { // caught while parsing the response
                    Log.e(TAG, "problem occurred")
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { volleyError -> // error occurred
                Log.e(TAG, "problem occurred, volley error: " + volleyError.message)
            }) {

            override fun getParams(): MutableMap<String, String> {
                return parameters;
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {

                val headers: MutableMap<String, String> = HashMap()
                // Add your Header paramters here
                return headers
            }
        }
        // Adding request to request queue
        volleyRequestQueue?.add(strReq)
    }
}