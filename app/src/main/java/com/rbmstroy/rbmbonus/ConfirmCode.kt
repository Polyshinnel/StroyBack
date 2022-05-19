package com.rbmstroy.rbmbonus

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.rbmstroy.rbmbonus.databinding.ActivityConfirmCodeBinding
import com.rbmstroy.rbmbonus.tools.SharedPreference
import org.json.JSONObject

class ConfirmCode : AppCompatActivity() {
    lateinit var binding: ActivityConfirmCodeBinding
    var phone = ""
    var token = ""
    var volleyRequestQueue: RequestQueue? = null
    var dialog: ProgressDialog? = null
    val serverAPIURL: String = "https://mobile.snabway.ru/api/controllers/ConfirmController.php"
    val TAG = "Response tag"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreference: SharedPreference = SharedPreference(this)
        phone = sharedPreference.getValueString("phone").toString()
        token = sharedPreference.getValueString("token").toString()

        binding.textCodePhone.text = "Мы позвоним на ваш номер: $phone. Кодом подтверждения являются последние 4 цифры"

        binding.confirmBtn.setOnClickListener {
            val code = binding.confirmCode.text.toString()
            sendConfirmCode(code, token)
        }
    }



    private fun sendConfirmCode(code: String, token: String) {
        volleyRequestQueue = Volley.newRequestQueue(this)
        dialog = ProgressDialog.show(this, "", "Отправка данных ...", true);
        val parameters: MutableMap<String, String> = HashMap()
        // Add your parameters in HashMap
        parameters.put("code",code);
        parameters.put("token",token);

        val strReq: StringRequest = object : StringRequest(
            Method.POST,serverAPIURL,
            Response.Listener { response ->
                Log.e(TAG, "response: " + response)
                dialog?.dismiss()

                // Handle Server response here
                try {
                    val responseObj = JSONObject(response)

                    val message = responseObj.getString("message")
                    if (message != "code ok") {
                        Toast.makeText(this,message, Toast.LENGTH_LONG).show()
                    }else{
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


