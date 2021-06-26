package com.jordandroid.aac

import android.app.Application
import android.content.ContentValues.TAG
import android.content.SharedPreferences
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_view.view.*
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import android.content.Context as Context1
import android.telephony.TelephonyManager.*
import android.util.TimeUtils
import android.widget.Toast
import com.google.android.material.internal.ContextUtils.getActivity
import java.util.concurrent.TimeUnit

public class Trajets{
    var listOfTrajets : ArrayList<Trajet> = ArrayList<Trajet>()
    var randomInt = ""

    fun addATrajet(trajet: Trajet){
        listOfTrajets.add(trajet)
        listOfTrajets.sortDescending()
    }

    fun toPrint(): String {
        var text = ""
        for (trajet in listOfTrajets){
            text += trajet.toPrint() + "<br> \n"
        }
        return text
    }
    fun toPrintTotal(context: Context1): String {
        var distMadeInt : Int =  0
        listOfTrajets.forEach { x ->distMadeInt += x.distance.toInt() }
        if (listOfTrajets.size >= 1){
            return "$distMadeInt ${settings.unit}"
        }
        else{
            return context.getString(R.string.no_trajet)
        }
    }
    override fun toString(): String {
        var text = ""
        for (trajet in listOfTrajets) {
            text += trajet.toString() + "\n"
        }
        return text
    }

    fun toJson(): String {
        val gson = Gson()

        println("Trajets JSON : ${gson.toJson(this)}")
        val json = AESUtils.encrypt(gson.toJson(this))
        println("jsonessai" + json)

        return json
    }

    fun saveFromExternalDB(sharedPreferences: SharedPreferences, id_text : String, context: android.content.Context) {
        settings.getValues(sharedPreferences)
        val database = FirebaseDatabase.getInstance().reference
        // Do network action in this function
        val gson = Gson()

        var dbManager =  TrajetsDB(context)

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val json = dataSnapshot.child(id_text).getValue(String::class.java)
                print("id_text : "+ id_text)
                println("Return SaveFromExternalDB : ${json}")
                var readed  = gson.fromJson(AESUtils.decrypt(json), Trajets::class.java)
                println("Trajets coucou "+readed.toPrintTotal(context))
                for (trajet in readed.listOfTrajets){
                    println("Trajets add : $trajet")
                    dbManager.Insert(trajet)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, databaseError.getMessage()) //Don't ignore errors!
                println("Return SaveFromExternalDB : Error")
            }
        }
        database.addListenerForSingleValueEvent(valueEventListener)

        // Write a message to the database
       /* lateinit var database: DatabaseReference

        database = Firebase.database.getReferenceFromUrl("https://time-to-drive-3c674.firebaseio.com/")

        database.child("StockedJSON").child(randomInt.toString())*/
    }


    fun saveToExternalDB(sharedPreferences: SharedPreferences, context : Context1){
        val json = toJson()
        val database = FirebaseDatabase.getInstance()


        settings.getValues(sharedPreferences)
        if (settings.id == "-1") {
            //val random = Random(System.nanoTime() % 100000)
            val time : Long  = System.currentTimeMillis()/(1000*3600)
            randomInt = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID) + time.toString(16) + //random.nextInt(1000000000)
            Toast.makeText(context, (randomInt).length, Toast.LENGTH_SHORT).show()
            // Write a message to the database
            val myRef = database.getReference(randomInt)
            myRef.setValue(json)
            settings.setID(sharedPreferences, randomInt)
        }
        else{
            val myRef = database.getReference(settings.id.toString())
            myRef.setValue(json)
        }
        /**database = Firebase.database.getReference("https://time-to-drive-3c674.firebaseio.com/")**/
    }
}