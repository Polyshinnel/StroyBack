package com.rbmstroy.rbmbonus

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.rbmstroy.rbmbonus.databinding.ActivityAceptCardBinding
import com.rbmstroy.rbmbonus.tools.SharedPreference
import org.json.JSONObject

class AceptCard : AppCompatActivity() {
    lateinit var binding: ActivityAceptCardBinding
    var token = ""
    var volleyRequestQueue: RequestQueue? = null
    var dialog: ProgressDialog? = null
    val serverAPIURL: String = "https://mobile.snabway.ru/api/controllers/AcceptCardStatus.php"
    val TAG = "Response tag"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAceptCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.headerAppCard, HeaderApp())
            .commit()

        val imgId = intent.getIntExtra("imgId", 0)
        val title = intent.getStringExtra("title")
        val balance = intent.getStringExtra("balance")

        binding.cardShopTitle.text = title
        binding.cardShopImg.setImageResource(imgId)
        binding.textBalance.text = balance

        binding.acceptCardBtn.setOnClickListener {
            val sharedPreference: SharedPreference = SharedPreference(this)

            token = sharedPreference.getValueString("token").toString()
            var cardName = title.toString()
            var cardBalance = balance.toString()
            AcceptCard(token,cardName,cardBalance)
        }
    }

    private fun AcceptCard(token: String, cardName: String, price: String) {
        volleyRequestQueue = Volley.newRequestQueue(this)
        dialog = ProgressDialog.show(this, "", "Отправка данных ...", true);
        val parameters: MutableMap<String, String> = HashMap()
        // Add your parameters in HashMap
        parameters.put("token",token);
        parameters.put("cardName",cardName);
        parameters.put("cardBalance", price)

        val strReq: StringRequest = object : StringRequest(
            Method.POST,serverAPIURL,
            Response.Listener { response ->
                Log.e(TAG, "response: " + response)
                dialog?.dismiss()

                // Handle Server response here
                try {
                    val responseObj = JSONObject(response)

                    val message = responseObj.getString("message")
                    if (message != "accept ok") {
                        Toast.makeText(this,"Что то пошло не так, попробуйте еще раз", Toast.LENGTH_LONG).show()
                    }else{
                        finish()
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