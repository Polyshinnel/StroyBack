package com.rbmstroy.rbmbonus

import android.app.ProgressDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.rbmstroy.rbmbonus.databinding.ActivityCardShopBinding
import com.rbmstroy.rbmbonus.tools.SharedPreference
import org.json.JSONObject

class CardShop : AppCompatActivity() {
    lateinit var binding: ActivityCardShopBinding
    var token = ""
    var volleyRequestQueue: RequestQueue? = null
    var dialog: ProgressDialog? = null
    val serverAPIURL: String = "https://mobile.snabway.ru/api/controllers/ShopCardController.php"
    val TAG = "Response tag"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardShopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.headerAppCard, HeaderApp())
            .commit()

        val imgId = intent.getIntExtra("imgId", 0)
        val title = intent.getStringExtra("title")

        binding.cardShopTitle.text = title
        binding.cardShopImg.setImageResource(imgId)

        val btn500 = binding.fivehundredBtn
        val btn1000 = binding.onethousandBtn
        val btn2000 = binding.twothausandBtn
        val btn5000 = binding.fivethousandBtn

        btn500.setOnClickListener {
            Log.d("MyLog2","Click!")
            with(btn500){
                setBackgroundColor(Color.parseColor("#283890"))
                setTextColor(Color.WHITE)
            }

            with(btn1000){
                setBackgroundColor(Color.parseColor("#ffffff"))
                setTextColor(Color.parseColor("#333333"))
            }

            with(btn2000){
                setBackgroundColor(Color.parseColor("#ffffff"))
                setTextColor(Color.parseColor("#333333"))
            }

            with(btn5000){
                setBackgroundColor(Color.parseColor("#ffffff"))
                setTextColor(Color.parseColor("#333333"))
            }

            binding.textBalance.text = "500 ₽"
        }

        btn1000.setOnClickListener {
            Log.d("MyLog2","Click!")
            with(btn500){
                setBackgroundColor(Color.parseColor("#ffffff"))
                setTextColor(Color.parseColor("#333333"))
            }

            with(btn1000){
                setBackgroundColor(Color.parseColor("#283890"))
                setTextColor(Color.parseColor("#ffffff"))
            }

            with(btn2000){
                setBackgroundColor(Color.parseColor("#ffffff"))
                setTextColor(Color.parseColor("#333333"))
            }

            with(btn5000){
                setBackgroundColor(Color.parseColor("#ffffff"))
                setTextColor(Color.parseColor("#333333"))
            }

            binding.textBalance.text = "1000 ₽"
        }

        btn2000.setOnClickListener {
            Log.d("MyLog2","Click!")
            with(btn500){
                setBackgroundColor(Color.parseColor("#ffffff"))
                setTextColor(Color.parseColor("#333333"))
            }

            with(btn1000){
                setBackgroundColor(Color.parseColor("#ffffff"))
                setTextColor(Color.parseColor("#333333"))
            }

            with(btn2000){
                setBackgroundColor(Color.parseColor("#283890"))
                setTextColor(Color.parseColor("#ffffff"))
            }

            with(btn5000){
                setBackgroundColor(Color.parseColor("#ffffff"))
                setTextColor(Color.parseColor("#333333"))
            }

            binding.textBalance.text = "2000 ₽"
        }

        btn5000.setOnClickListener {
            Log.d("MyLog2","Click!")
            with(btn500){
                setBackgroundColor(Color.parseColor("#ffffff"))
                setTextColor(Color.parseColor("#333333"))
            }

            with(btn1000){
                setBackgroundColor(Color.parseColor("#ffffff"))
                setTextColor(Color.parseColor("#333333"))
            }

            with(btn2000){
                setBackgroundColor(Color.parseColor("#ffffff"))
                setTextColor(Color.parseColor("#333333"))
            }

            with(btn5000){
                setBackgroundColor(Color.parseColor("#283890"))
                setTextColor(Color.parseColor("#ffffff"))
            }

            binding.textBalance.text = "5000 ₽"
        }

        binding.shopCard.setOnClickListener {
            val sharedPreference: SharedPreference = SharedPreference(this)

            token = sharedPreference.getValueString("token").toString()
            val price = binding.textBalance.text.toString()
            val cardName = binding.cardShopTitle.text.toString()

            shopCard(token,cardName,price)

        }


        binding.cardShopBack.setOnClickListener {
            finish()
        }
    }

    private fun shopCard(token: String, cardName: String, price: String) {
        volleyRequestQueue = Volley.newRequestQueue(this)
        dialog = ProgressDialog.show(this, "", "Отправка данных ...", true);
        val parameters: MutableMap<String, String> = HashMap()
        // Add your parameters in HashMap
        parameters.put("token",token);
        parameters.put("cardName",cardName);
        parameters.put("price", price)

        val strReq: StringRequest = object : StringRequest(
            Method.POST,serverAPIURL,
            Response.Listener { response ->
                Log.e(TAG, "response: " + response)
                dialog?.dismiss()

                // Handle Server response here
                try {
                    val responseObj = JSONObject(response)

                    val message = responseObj.getString("message")
                    if (message != "shop ok") {
                        binding.balanceLayer.visibility = View.GONE
                        binding.cardValueBtn.visibility = View.GONE
                        binding.shopCard.visibility = View.GONE

                        binding.successLayout.visibility = View.VISIBLE
                        binding.shopCardStateImg.setImageResource(R.drawable.ic_error_card)
                        binding.shopCardStateText.text ="На вашем балансе недостаточно средств!"
                        binding.cardShopTextNotice.visibility = View.VISIBLE
                        binding.cardShopTextNotice.text = "К сожалению на Вашем балансе не хватает средств, для заказа данной карты, пожалуйста выберите карту меньшего номинала."
                        binding.cardShopBack.visibility = View.VISIBLE

                    }else{
                        binding.balanceLayer.visibility = View.GONE
                        binding.cardValueBtn.visibility = View.GONE
                        binding.shopCard.visibility = View.GONE

                        binding.successLayout.visibility = View.VISIBLE
                        binding.cardShopTextNotice.visibility = View.VISIBLE
                        binding.cardShopBack.visibility = View.VISIBLE
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


