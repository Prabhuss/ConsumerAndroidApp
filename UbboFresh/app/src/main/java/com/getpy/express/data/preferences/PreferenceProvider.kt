package com.getpy.express.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class PreferenceProvider(context: Context) {
    private val appicationcontxt=context.applicationContext
    private val preference:SharedPreferences
      get() = PreferenceManager.getDefaultSharedPreferences(appicationcontxt)

    fun saveStringData(key:String,value:String)
    {
        preference.edit().putString(key,value).apply()
    }
    fun getStringData(key:String):String
    {
        return preference.getString(key,"").toString()
    }

    fun saveIntData(key:String,value:Int)
    {
        preference.edit().putInt(key,value).apply()
    }
    fun getIntData(key:String):Int
    {
        return preference.getInt(key,0)
    }
    fun saveBoolData(key:String,value:Boolean)
    {
        preference.edit().putBoolean(key,value).apply()
    }
    fun getBoolData(key:String):Boolean
    {
        return preference.getBoolean(key,false)
    }

}