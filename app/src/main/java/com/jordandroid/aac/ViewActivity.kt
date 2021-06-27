package com.jordandroid.aac

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Log
import android.view.Menu
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_view.*
import java.lang.IndexOutOfBoundsException


class ViewActivity : AppCompatActivity() {
    var trajets = Trajets()
    var dbManager =  TrajetsDB(this)

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var constraintLayout : ConstraintLayout
    private lateinit var adapter : TrajetsRecyclerViewAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {

        val sharedPref: SharedPreferences = getSharedPreferences(CustomSettings.PREF_NAME, CustomSettings.PRIVATE_MODE)
        CustomSettings.getValues(sharedPref)
        val isNightModeEnabled = sharedPref.getBoolean(CustomSettings.DARK_MODE, true)

        if (isNightModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)
        setSupportActionBar(findViewById(R.id.toolbar))


        CustomSettings.getValues(sharedPref)
        constraintLayout = findViewById(R.id.constraintLayout)
        linearLayoutManager = LinearLayoutManager(this)
        rv_trajets.layoutManager = linearLayoutManager

        recyclerView = findViewById(R.id.rv_trajets)

        add_trajet.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        refreshView()
        enableSwipeToDeleteAndUndo()

    }

    override fun onResume() {  // After a pause OR at startup
        super.onResume()

        val sharedPref: SharedPreferences = getSharedPreferences(CustomSettings.PREF_NAME, CustomSettings.PRIVATE_MODE)
        CustomSettings.getValues(sharedPref)
        val isNightModeEnabled = sharedPref.getBoolean(CustomSettings.DARK_MODE, true)

        if (isNightModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        //Refresh your stuff here
        refreshView()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        val actionSettings = menu.findItem(R.id.action_settings)
        actionSettings.setOnMenuItemClickListener{
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            (true)
        }

        var mWebView: WebView? = null

        fun createWebPrintJob(webView: WebView) {
            // Get a PrintManager instance
            (this.getSystemService(Context.PRINT_SERVICE) as? PrintManager)?.let { printManager ->

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
            // Create a WebView object specifically for printing
            val webView = WebView(this)
            webView.webViewClient = object : WebViewClient() {

                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest) = false

                override fun onPageFinished(view: WebView, url: String) {
                    Log.i(ContentValues.TAG, "page finished loading $url")
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

        val actionPrint = menu.findItem(R.id.action_print)
        actionPrint.setOnMenuItemClickListener{
            // Get a PrintManager instance
            doWebViewPrint()
            doWebViewPrint()
            (true)
        }

        return true
    }


    private fun enableSwipeToDeleteAndUndo() {
        val swipeToDeleteCallback: SwipeToDeleteCallback = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
                try {
                    val position = viewHolder.adapterPosition
                    val trajet = adapter.getItem(position)
                    var res = dbManager.deleteTrajet(trajet)
                    val snackbar = Snackbar.make(constraintLayout, getString(R.string.delete_trajet), Snackbar.LENGTH_LONG)
                    snackbar.setAction(getString(R.string.undo), object : View.OnClickListener {
                        override fun onClick(view: View?) {
                            dbManager.Insert(trajet)
                            refreshView()
                        }
                    })
                    snackbar.setTextColor(resources.getColor(R.color.colorPrimaryDark))
                    snackbar.setActionTextColor(resources.getColor(R.color.colorAccent))
                    snackbar.show()
                }
                catch( e : IndexOutOfBoundsException){
                    print("Issue with index $e")
                }
                refreshView()
            }
        }
        val itemTouchhelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchhelper.attachToRecyclerView(recyclerView)
    }

    fun refreshView(){
        trajets = dbManager.loadTrajets()
        adapter = TrajetsRecyclerViewAdapter(trajets.listOfTrajets)
        recyclerView.adapter = adapter

        var distMadeInt =  0
        trajets.listOfTrajets.forEach { x ->distMadeInt += x.distance.toInt() }

        tv_distanceMade.text = "$distMadeInt ${CustomSettings.unit}"

        val objectifInt : Int = CustomSettings.objectif
        tv_objectif.text = "$objectifInt ${CustomSettings.unit}"

        val progress : Int = (distMadeInt*100/objectifInt)
        progressBarId.max = 100
        progressBarId.setProgress(progress, false)

    }

}