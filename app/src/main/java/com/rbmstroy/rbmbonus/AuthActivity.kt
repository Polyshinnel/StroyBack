package com.rbmstroy.rbmbonus

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.rbmstroy.rbmbonus.databinding.ActivityAuthBinding
import com.rbmstroy.rbmbonus.tools.SharedPreference
import org.json.JSONObject

class AuthActivity : AppCompatActivity() {
    lateinit var binding: ActivityAuthBinding
    var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPreference: SharedPreference = SharedPreference(this)

        token = sharedPreference.getValueString("token").toString()
        Log.d("token",token);
        if(token != "null"){
            startActivity(Intent(this, MainActivity::class.java))
        }



        binding.loginBtn.setOnClickListener {
            var email = binding.userMail.text.toString()
            var pass = binding.userPass.text.toString()
            sendSignInDataToServer(email, pass)
        }

        binding.registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.fogottenLink.setOnClickListener {
            startActivity(Intent(this,ForgottenActivity::class.java))
        }



        binding.supportPhone.setOnClickListener {
            getCallPermission()
        }
    }

    override fun onResume() {
        super.onResume()
        if(token != "null"){
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun getCallPermission(){
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.CALL_PHONE), 102)
            return
        }

        startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:+79036365857")))
    }

    var volleyRequestQueue: RequestQueue? = null
    var dialog: ProgressDialog? = null
    val serverAPIURL: String = "https://mobile.snabway.ru/api/controllers/AuthController.php"
    val TAG = "Response tag"

    private fun sendSignInDataToServer(email: String, password: String) {
        volleyRequestQueue = Volley.newRequestQueue(this)
        dialog = ProgressDialog.show(this, "", "Отправка данных ...", true);
        val parameters: MutableMap<String, String> = HashMap()
        // Add your parameters in HashMap
        parameters.put("email",email);
        parameters.put("pass",password);

        val strReq: StringRequest = object : StringRequest(
            Method.POST,serverAPIURL,
            Response.Listener { response ->
                Log.e(TAG, "response: " + response)
                dialog?.dismiss()

                // Handle Server response here
                try {
                    val responseObj = JSONObject(response)

                    val message = responseObj.getString("message")
                    if (message != "auth ok") {
                        Toast.makeText(this,message,Toast.LENGTH_LONG).show()
                    }else{
                        val token = responseObj.getString("token")
                        val sharedPreference: SharedPreference = SharedPreference(this)
                        sharedPreference.save("token",token)
                        startActivity(Intent(this, MainActivity::class.java))
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


