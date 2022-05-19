package com.rbmstroy.rbmbonus

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.rbmstroy.rbmbonus.adapters.MyPagerAdapter
import com.rbmstroy.rbmbonus.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.tabs.TabLayoutMediator
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.rbmstroy.rbmbonus.tools.SharedPreference
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MyPagerAdapter
    private lateinit var sharedPreference: SharedPreference
    var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Получаем геолокацию(не забыть передать контекст)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fetchLocation()

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.headerHolder, HeaderApp())
            .commit()

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.UnPayedCard, UnpayedCard())
            .commit()

        getCallPermission()

        initTab()

        val sharedPreference = SharedPreference(this)

        token = sharedPreference.getValueString("token").toString()

        getUserInfo(token)
    }

    override fun onResume() {
        super.onResume()
        val sharedPreference: SharedPreference = SharedPreference(this)

        token = sharedPreference.getValueString("token").toString()

        getUserInfo(token)
    }

    private fun getCallPermission(){
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.CALL_PHONE), 102)
            return
        }
    }


    //Функция получения геолокации
    private fun fetchLocation() {
        val task = fusedLocationProviderClient.lastLocation

        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 101)
            return
        }

        task.addOnSuccessListener {
            if(it != null){
                Log.d("MyLog","${it.latitude} ${it.longitude}")
            }else{
                Log.d("MyLog","Не получается определить текущие координаты")
            }
        }
    }

    //Функция получения разрешения на камеру и ее открытие
    private fun checkCameraPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),12)
        }else{
            startActivity(Intent(this, QrScanner::class.java))
        }
    }

    //Слушатель для кнопки запуска QR Activity
    fun startScanQr(view:View){
        checkCameraPermission()
    }

    fun startPrice(view: View){
        startActivity(Intent(this,ProductListActivity::class.java))
    }

    //Функция инициализации запуска табов
    private fun initTab(){
        adapter = MyPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabsMain,binding.viewPager){tab,position->
            when(position){
                0->tab.text = "Подарочные карты"
                else->tab.text = "История"
            }

        }.attach()
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 12){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startActivity(Intent(this, QrScanner::class.java))
            }
        }
    }

    //
    private fun getUserInfo(token: String) {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url: String = "https://mobile.snabway.ru/api/controllers/UserController.php/?token=$token"

        // Request a string response from the provided URL.
        val stringReq = StringRequest(Request.Method.GET, url,
            Response.Listener<String> { response ->

                var strResp = response.toString()
                val jsonObj: JSONObject = JSONObject(strResp)
                var message = jsonObj.getString("message")
                if(message.toString() == "ok"){
                    val balance = jsonObj.getString("balance")
                    val name = jsonObj.getString("username")
                    var requedStatus = jsonObj.getString("requedCards")
                    requedStatus = requedStatus.toString()

                    Log.d("TestMSG",requedStatus)

                    binding.balanceText.text = balance+" ₽"

                    if(requedStatus == "yes"){
                        binding.UnPayedCard.visibility = View.VISIBLE
                    }else{
                        binding.UnPayedCard.visibility = View.GONE
                    }
                }else{
                    val sharedPreference = SharedPreference(this)
                    sharedPreference.removeValue("token")
                }


            },
            Response.ErrorListener { Log.d("JsonLog","That didn't work!") })
        queue.add(stringReq)
    }
}

