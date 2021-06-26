package com.jordandroid.aac

import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    var dbManager =  TrajetsDB(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);
        supportActionBar!!.setDisplayShowHomeEnabled(true);

        val sharedPref: SharedPreferences = getSharedPreferences(settings.PREF_NAME, settings.PRIVATE_MODE)
        settings.getValues(sharedPref)
        var isNightModeEnabled = sharedPref.getBoolean(settings.DARK_MODE, true);

        if (isNightModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState)

        fun onClickAddTrajet(){
            var newTrajet : Trajet = Trajet()

            newTrajet.trajet = trajet_title.text.toString()
            newTrajet.date = datePicker.text.toString()

            if (newTrajet.trajet.toString().trim() == ""){
                Toast.makeText(this, getString(R.string.empty_trajet_name), Toast.LENGTH_LONG).show()
            }
            else if (!validate(newTrajet.date)){
                Toast.makeText(this, getString(R.string.date_isnotValid), Toast.LENGTH_LONG).show()
            }
            else if (newTrajet.trajet.split(";").size >1){
                Toast.makeText(this, getString(R.string.trajet_name_notcorrect), Toast.LENGTH_LONG).show()
            }
            else {
                try {
                    newTrajet.distance = Integer(trajet_km.text.toString())
                }
                catch (e: NumberFormatException){
                    Toast.makeText(this, getString(R.string.distance_isnotvalid), Toast.LENGTH_LONG).show()
                }
                dbManager.Insert(newTrajet)
                Toast.makeText(this, getString(R.string.trajet_add), Toast.LENGTH_LONG).show()
                trajet_km.setText("")
                trajet_title.setText("")
            }
        }
        button.setOnClickListener { onClickAddTrajet() }


        val myCalendar: Calendar = Calendar.getInstance()

        fun updateLabel() {
            var myFormat = "dd/MM/YYYY"; //In which you need put here
            var sdf = SimpleDateFormat(myFormat, Locale.FRANCE);

            datePicker.setText(sdf.format(myCalendar.getTime()));
        }

        val date =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth -> // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLabel()
            }

        datePicker.setOnClickListener { v -> DatePickerDialog(this@MainActivity, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show() }
    }

    /**
     * Validate date format with regular expression
     * @param date date address for validation
     * @return true valid date format, false invalid date format
     */
    open fun validate(date: String): Boolean {
        try {
            var list = date.split("/")
            var day = list[0].toInt()
            var month = list[1].toInt()
            var year = list[2].toInt()

             return ((day >0) and (day<= 31) and (month >0) and (month <= 12))
        }
        catch (e: Exception){
            println("Exception was catched")
           return false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)

        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()!!.setDisplayShowHomeEnabled(true);

        var actionSettings = menu.findItem(R.id.action_settings)
        actionSettings.setOnMenuItemClickListener{
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            (true)
        }

        var mWebView: WebView? = null

        fun createWebPrintJob(webView: WebView) {
            // Get a PrintManager instance
            (this!!.getSystemService(Context.PRINT_SERVICE) as? PrintManager)?.let { printManager ->

                val jobName = "${getString(R.string.app_name)} Document"

                // Get a print adapter instance
                val printAdapter = webView.createPrintDocumentAdapter(jobName)

                // Create a print job with name and adapter instance
                printManager.print(
                    jobName,
                    printAdapter,
                    PrintAttributes.Builder().build()
                )
            }
        }
        fun doWebViewPrint() {
            var trajets = Trajets()
            trajets = dbManager.loadTrajets()
            // Create a WebView object specifically for printing
            val webView = WebView(this)
            webView.webViewClient = object : WebViewClient() {

                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest) = false

                override fun onPageFinished(view: WebView, url: String) {
                    Log.i(TAG, "page finished loading $url")
                    createWebPrintJob(view)
                    mWebView = null
                }
            }

            // Generate an HTML document on the fly:
            val htmlDocument = "<html><body><h1>"+getString(R.string.print_title)+"</h1><p>${trajets.toPrint()} </p><h2>"+getString(R.string.print_sum)+"${trajets.toPrintTotal(this)}  </h2></body></html>"
            webView.loadDataWithBaseURL(null, htmlDocument, "text/HTML", "UTF-8", null)

            // Keep a reference to WebView object until you pass the PrintDocumentAdapter
            // to the PrintManager
            mWebView = webView
        }

        var actionPrint = menu.findItem(R.id.action_print)
        actionPrint.setOnMenuItemClickListener{
                // Get a PrintManager instance
                doWebViewPrint()
                (true)
            }

        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.home -> {
                finish()
                super.onOptionsItemSelected(item)
            }
            16908332 -> {
                finish()
                super.onOptionsItemSelected(item)
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()

        val sharedPref: SharedPreferences = getSharedPreferences(settings.PREF_NAME, settings.PRIVATE_MODE)
        settings.getValues(sharedPref)
        var isNightModeEnabled = sharedPref.getBoolean(settings.DARK_MODE, true);

        if (isNightModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}

