package com.jordandroid.aac

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences


object settings{
    val PRIVATE_MODE = 0
    val PREF_NAME = "aac"
    val DARK_MODE = "DARK_MODE"
    val OBJECTIF = "OBJECTIF"
    val UNIT = "UNIT"
    val ID = "ID"

    var objectif = 3000
    var unit = "km"
    var darkMode = false
    var id = "-1"

    fun getValues(sharedPref: SharedPreferences ) {
        settings.darkMode = sharedPref.getBoolean(settings.DARK_MODE, settings.darkMode)
        settings.objectif = sharedPref.getInt(settings.OBJECTIF, settings.objectif)
        settings.unit = sharedPref.getString(settings.UNIT, settings.unit)!!
        settings.id= sharedPref.getString(settings.ID, settings.id)!!
    }

    fun setID(sharedPref : SharedPreferences, storeId : String){
        var editor = sharedPref.edit();
        settings.id = storeId
        editor.putString(settings.ID, id);
        editor.apply();
    }
}