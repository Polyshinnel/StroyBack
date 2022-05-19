package com.rbmstroy.rbmbonus.tools

import android.content.Context
import android.content.SharedPreferences

class SharedPreference(val context: Context) {
    private val PREFS_NAME = "table"
    val sharedPref: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun save(KEY_NAME: String, text: String){
        val editor : SharedPreferences.Editor = sharedPref.edit()
        editor.putString(KEY_NAME,text)
        editor!!.commit()
    }

    fun getValueString(KEY_NAME: String): String?{
        return sharedPref.getString(KEY_NAME,null)
    }

    fun removeValue(KEY_NAME: String){
        val editor : SharedPreferences.Editor = sharedPref.edit()
        editor.remove(KEY_NAME)
        editor.commit()
    }
}