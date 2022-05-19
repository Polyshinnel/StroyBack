package com.rbmstroy.rbmbonus

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.rbmstroy.rbmbonus.databinding.ActivityForgottenBinding
import com.rbmstroy.rbmbonus.tools.SharedPreference
import org.json.JSONObject

class ForgottenActivity : AppCompatActivity() {
    lateinit var binding: ActivityForgottenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgottenBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        val sharedPreference: SharedPreference = SharedPreference(this)



        binding.sendUserPhone.setOnClickListener {
            val phone = binding.userPhone.text.toString()
            sendingForgottenPhone(phone)
        }

        binding.sendForgotenCode.setOnClickListener {
            val userCode = binding.forgottenCode.text.toString()
            val codeConf = sharedPreference.getValueString("code").toString()
            if(userCode == codeConf){
                binding.forgotenCodeBlock.visibility = View.GONE
                binding.forgottenPassBlock.visibility = View.VISIBLE
            }else{
                Toast.makeText(this, "Не верный код потверждения!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.sendNewPass.setOnClickListener {
            val phoneConf = sharedPreference.getValueString("phoneUser").toString()
            val pass = binding.passInput.text.toString()
            val confirmPass = binding.confirmPassInput.text.toString()
            if(pass == confirmPass){
                updateForgottenCode(phoneConf,pass)
            }else{
                Toast.makeText(this, "Пароли не совпадают!", Toast.LENGTH_SHORT).show()
            }

        }


        binding.backBtn.setOnClickListener {
            finish()
        }


    }



    private fun sendingForgottenPhone(phone: String) {
        var volleyRequestQueue: RequestQueue? = null
        var dialog: ProgressDialog? = null
        val serverAPIURL: String = "https://mobile.snabway.ru/api/controllers/ForgottenPhoneController.php"
        val TAG = "Response tag"

        volleyRequestQueue = Volley.newRequestQueue(this)
        dialog = ProgressDialog.show(this, "", "Отправка данных ...", true);
        val parameters: MutableMap<String, String> = HashMap()
        // Add your parameters in HashMap
        parameters.put("phone",phone);

        val strReq: StringRequest = object : StringRequest(
            Method.POST,serverAPIURL,
            Response.Listener { response ->
                Log.e(TAG, "response: " + response)
                dialog?.dismiss()

                // Handle Server response here
                try {
                    val responseObj = JSONObject(response)

                    val message = responseObj.getString("message")
                    if (message != "status ok") {
                        Toast.makeText(this,message, Toast.LENGTH_LONG).show()
                    }else{
                        val code = responseObj.getString("code")
                        val sharedPreference: SharedPreference = SharedPreference(this)
                        sharedPreference.save("phoneUser",phone)
                        sharedPreference.save("code",code)
                        binding.forgotenPhoneBlock.visibility = View.GONE
                        binding.forgotenCodeBlock.visibility = View.VISIBLE
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

    private fun updateForgottenCode(phone: String,pass: String) {
        var volleyRequestQueue: RequestQueue? = null
        var dialog: ProgressDialog? = null
        val serverAPIURL: String = "https://mobile.snabway.ru/api/controllers/UpdatePassController.php"
        val TAG = "Response tag"

        volleyRequestQueue = Volley.newRequestQueue(this)
        dialog = ProgressDialog.show(this, "", "Отправка данных ...", true);
        val parameters: MutableMap<String, String> = HashMap()
        // Add your parameters in HashMap
        parameters.put("phone",phone);
        parameters.put("pass",pass);

        val strReq: StringRequest = object : StringRequest(
            Method.POST,serverAPIURL,
            Response.Listener { response ->
                Log.e(TAG, "response: " + response)
                dialog?.dismiss()

                // Handle Server response here
                try {
                    val responseObj = JSONObject(response)

                    val message = responseObj.getString("message")
                    if (message != "status ok") {
                        Toast.makeText(this,message, Toast.LENGTH_LONG).show()
                    }else{
                        startActivity(Intent(this,AuthActivity::class.java))
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