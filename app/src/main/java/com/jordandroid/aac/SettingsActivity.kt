package com.jordandroid.aac

import android.content.ClipData
import android.content.ClipboardManager
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import kotlinx.android.synthetic.main.activity_settings.*
import java.lang.RuntimeException


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPref: SharedPreferences = getSharedPreferences(
            CustomSettings.PREF_NAME,
            CustomSettings.PRIVATE_MODE
        )
        CustomSettings.getValues(sharedPref)
        val isNightModeEnabled = sharedPref.getBoolean(CustomSettings.DARK_MODE, true)
        if (isNightModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        edit_objectif.setText(CustomSettings.objectif.toString())

        if (CustomSettings.unit == "km"){
            radiobutton_km.isChecked = true
        }
        else{
            radiobutton_mile.isChecked = true
        }
        FirebaseApp.initializeApp(/*context=*/ this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(SafetyNetAppCheckProviderFactory.getInstance())

        sw_darkmode.isChecked = isNightModeEnabled


        validateChange.setOnClickListener {
                try {
                    CustomSettings.objectif = edit_objectif.text.toString().trim().toInt()
                    val editor = sharedPref.edit()
                    editor.putInt(CustomSettings.OBJECTIF, CustomSettings.objectif)
                    editor.apply()
                    Toast.makeText(this, getString(R.string.updated_obj), Toast.LENGTH_LONG).show()
                }
                catch (e: NumberFormatException){
                    print("Number format seems wrong, keep the default value")
                }
            }

        radio_unit.setOnCheckedChangeListener { _, i ->
            if (i == R.id.radiobutton_mile) {
                CustomSettings.unit = "miles"
                Toast.makeText(this, getString(R.string.unit_change_miles), Toast.LENGTH_LONG).show()
            } else {
                CustomSettings.unit = "km"
                Toast.makeText(this, getString(R.string.unit_change_km), Toast.LENGTH_LONG).show()
            }
            val editor = sharedPref.edit()
            editor.putString(CustomSettings.UNIT, CustomSettings.unit)
            editor.apply()
        }


        sw_darkmode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
            {
                val editor = sharedPref.edit()
                editor.putBoolean(CustomSettings.DARK_MODE, true)
                editor.apply()
                Toast.makeText(this, getString(R.string.darkMode_applied), Toast.LENGTH_LONG).show()
            }
            else {
                val editor = sharedPref.edit()
                editor.putBoolean(CustomSettings.DARK_MODE, false)
                editor.apply()
                Toast.makeText(this, getString(R.string.darkMode_deactivate), Toast.LENGTH_LONG).show()
            }
            finish()
        }

        if (CustomSettings.id == "-1"){
            tv_id.text =  getString(R.string.noBackupGenerated)
        }
        else{
            val text = CustomSettings.id + "_" + CustomSettings.id_s
            tv_id.text =  text
        }

        val trajets: Trajets

        val dbManager =  TrajetsDB(this)
        trajets = dbManager.loadTrajets()

        layout_click_ID.setOnClickListener {
            if (CustomSettings.id != "-1") {
                val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("ID backup", tv_id.text)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this,"Copied to clipboard", Toast.LENGTH_LONG).show()
            } else {
                trajets.saveToExternalDB(sharedPref, this)
                CustomSettings.getValues(sharedPref)
                val text = CustomSettings.id + "_" + CustomSettings.id_s
                tv_id.text =  text
                Toast.makeText(this,"Generated a new backup", Toast.LENGTH_LONG).show()
            }
        }

        generateID.setOnClickListener {
            trajets.saveToExternalDB(sharedPref, this)
            val text = CustomSettings.id + "_" + CustomSettings.id_s
            tv_id.text =  text
            Toast.makeText(this,"Generated a new backup", Toast.LENGTH_LONG).show()
        }

        getID.setOnClickListener {
            try {
                val mId = edit_id.text.split("_")[0]
                val mId_s = edit_id.text.split("_")[1]
                trajets.saveFromExternalDB(sharedPref, mId, mId_s, this)
                Toast.makeText(this, getString(R.string.getID), Toast.LENGTH_LONG).show()
                edit_id.setText("")
            }
            catch (e : Exception){
                Toast.makeText(this, getString(R.string.getID_FAILED), Toast.LENGTH_LONG).show()
            }
            catch (e : RuntimeException){
                Toast.makeText(this, getString(R.string.getID_FAILED), Toast.LENGTH_LONG).show()
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.itemId === android.R.id.home) {
            finish() // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item)
    }
}