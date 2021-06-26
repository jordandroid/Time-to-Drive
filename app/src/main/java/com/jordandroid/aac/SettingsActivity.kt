package com.jordandroid.aac

import android.R.attr.label
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


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPref: SharedPreferences = getSharedPreferences(
            settings.PREF_NAME,
            settings.PRIVATE_MODE
        )
        settings.getValues(sharedPref)
        val isNightModeEnabled = sharedPref.getBoolean(settings.DARK_MODE, true);
        if (isNightModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(findViewById(R.id.toolbar))
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()!!.setDisplayShowHomeEnabled(true);

        edit_objectif.setText(settings.objectif.toString())

        if (settings.unit == "km"){
            radiobutton_km.isChecked = true
        }
        else{
            radiobutton_mile.isChecked = true
        }
        FirebaseApp.initializeApp(/*context=*/ this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(SafetyNetAppCheckProviderFactory.getInstance())

        sw_darkmode.isChecked = isNightModeEnabled


        validateChange.setOnClickListener() {
                try {
                    settings.objectif = edit_objectif.text.toString().trim().toInt()
                    val editor = sharedPref.edit()
                    editor.putInt(settings.OBJECTIF, settings.objectif)
                    editor.apply()
                    Toast.makeText(this, getString(R.string.updated_obj), Toast.LENGTH_LONG).show()
                }
                catch (e: NumberFormatException){
                    print("Number format seems wrong, keep the default value")
                }
            }

        radio_unit.setOnCheckedChangeListener { _, i ->
            if (i == R.id.radiobutton_mile) {
                settings.unit = "miles"
                Toast.makeText(this, getString(R.string.unit_change_miles), Toast.LENGTH_LONG).show()
            } else {
                settings.unit = "km"
                Toast.makeText(this, getString(R.string.unit_change_km), Toast.LENGTH_LONG).show()
            }
            val editor = sharedPref.edit()
            editor.putString(settings.UNIT, settings.unit)
            editor.apply()
        }


        sw_darkmode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
            {
                var editor = sharedPref.edit();
                editor.putBoolean(settings.DARK_MODE, true);
                editor.apply();
                Toast.makeText(this, getString(R.string.darkMode_applied), Toast.LENGTH_LONG).show()
            }
            else {
                var editor = sharedPref.edit();
                editor.putBoolean(settings.DARK_MODE, false);
                editor.apply();
                Toast.makeText(this, getString(R.string.darkMode_deactivate), Toast.LENGTH_LONG).show()
            }
            finish()
        }

        if (settings.id == "-1"){
            tv_id.text =  getString(R.string.noBackupGenerated)
        }
        else{
            tv_id.text = settings.id.toString();
        }

        var trajets = Trajets()

        var dbManager =  TrajetsDB(this)
        trajets = dbManager.loadTrajets()


        layout_click_ID.setOnClickListener() {
            if (settings.id != "-1") {
                var clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("ID backup", settings.id.toString())
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this,"Copied to clipboard", Toast.LENGTH_LONG).show()
            } else {
                trajets.saveToExternalDB(sharedPref, this)
                settings.getValues(sharedPref)
                tv_id.text = settings.id.toString();
                Toast.makeText(this,"Generated a new backup", Toast.LENGTH_LONG).show()
            }
        }

        generateID.setOnClickListener() {
            trajets.saveToExternalDB(sharedPref, this)
            tv_id.text = settings.id.toString();
            Toast.makeText(this,"Generated a new backup", Toast.LENGTH_LONG).show()
        }

        getID.setOnClickListener(){
            trajets.saveFromExternalDB(sharedPref, edit_id.text.toString(), this)
            Toast.makeText(this,getString(R.string.getID), Toast.LENGTH_LONG).show()
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.getItemId() === android.R.id.home) {
            finish() // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item)
    }
}