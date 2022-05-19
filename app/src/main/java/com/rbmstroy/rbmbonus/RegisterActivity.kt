package com.rbmstroy.rbmbonus

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.rbmstroy.rbmbonus.databinding.ActivityRegisterBinding
import com.rbmstroy.rbmbonus.tools.SharedPreference
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var volleyRequestQueue: RequestQueue? = null
    var dialog: ProgressDialog? = null
    val serverAPIURL: String = "https://mobile.snabway.ru/api/controllers/RegisterController.php"
    val TAG = "Response tag"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fetchLocation()
    }

    override fun onResume() {
        super.onResume()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fetchLocation()

        binding.backBtn2.setOnClickListener{
            finish()
        }
    }

    fun getRegister(view: View){
        val geo = binding.geoReg.text.toString()
        val username = binding.registerName.text.toString()
        val phone = binding.registerPhone.text.toString()
        val organization = binding.registerOrganization.text.toString()
        val email = binding.registerMail.text.toString()
        val password = binding.registerPass.text.toString()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fetchLocation()
        sendSignUpDataToServer(username,organization,phone,geo,email,password)
    }

    private fun sendSignUpDataToServer(username : String,organization: String, phone: String , geo: String,email: String, password: String) {
        volleyRequestQueue = Volley.newRequestQueue(this)
        dialog = ProgressDialog.show(this, "", "Отправка данных ...", true);
        val parameters: MutableMap<String, String> = HashMap()
        // Add your parameters in HashMap
        parameters.put("username",username);
        parameters.put("organization",organization);
        parameters.put("mail",email);
        parameters.put("phone",phone);
        parameters.put("pass",password);
        parameters.put("geo",geo);


        val strReq: StringRequest = object : StringRequest(
            Method.POST,serverAPIURL,
            Response.Listener { response ->
                Log.e(TAG, "response: " + response)
                dialog?.dismiss()

                // Handle Server response here
                try {
                    val responseObj = JSONObject(response)

                    val message = responseObj.getString("message")
                    if (message != "Register success") {
                        Toast.makeText(this,message, Toast.LENGTH_LONG).show()
                    }else{
                        val token = responseObj.getString("token")
                        val sharedPreference: SharedPreference = SharedPreference(this)
                        sharedPreference.save("token",token)
                        sharedPreference.save("phone",phone)
                        startActivity(Intent(this, ConfirmCode::class.java))
                        Toast.makeText(this,message, Toast.LENGTH_LONG).show()
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

    private fun fetchLocation() {
        val task = fusedLocationProviderClient.lastLocation

        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION), 101)
            return
        }

        task.addOnSuccessListener {
            if(it != null){
                Log.d("MyLog","${it.latitude} ${it.longitude}")
                binding.geoReg.text = "${it.latitude},${it.longitude}";
            }else{
                Log.d("MyLog","Не получается определить текущие координаты")
            }
        }
    }

}