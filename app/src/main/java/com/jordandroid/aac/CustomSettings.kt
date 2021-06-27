package com.jordandroid.aac

import android.content.SharedPreferences


object CustomSettings{
    const val PRIVATE_MODE = 0
    const val PREF_NAME = "aac"
    const val DARK_MODE = "DARK_MODE"
    const val OBJECTIF = "OBJECTIF"
    const val UNIT = "UNIT"
    const val ID = "ID"
    const val ID_S = "ID_S"

    var objectif = 3000
    var unit = "km"
    var darkMode = false
    var id = "-1"
    var id_s = ""

    fun getValues(sharedPref: SharedPreferences ) {
        darkMode = sharedPref.getBoolean(DARK_MODE, darkMode)
        objectif = sharedPref.getInt(OBJECTIF, objectif)
        unit = sharedPref.getString(UNIT, unit)!!
        id = sharedPref.getString(ID, id)!!
        id_s = sharedPref.getString(ID_S, id_s)!!
    }

    fun setID(sharedPref : SharedPreferences, storeId : String){
        val editor = sharedPref.edit()
        id = storeId
        editor.putString(ID, id)
        editor.apply()
    }
    fun setIDS(sharedPref : SharedPreferences, storeIdS: String){
        val editor = sharedPref.edit()
        id_s = storeIdS
        editor.putString(ID_S, id_s)
        editor.apply()
    }
}