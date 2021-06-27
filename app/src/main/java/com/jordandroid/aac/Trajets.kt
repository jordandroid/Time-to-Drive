package com.jordandroid.aac

import android.content.ContentValues.TAG
import android.content.SharedPreferences
import android.provider.Settings
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import java.util.*
import kotlin.collections.ArrayList
import android.content.Context as Context1
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory

class Trajets{
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
        var distMadeInt =  0
        listOfTrajets.forEach { x ->distMadeInt += x.distance.toInt() }
        if (listOfTrajets.size >= 1){
            return "$distMadeInt ${CustomSettings.unit}"
        }
        else{
            return context.getString(R.string.no_trajet)
        }
    }
    override fun toString(): String {
        var text = ""
        if (listOfTrajets.size < 10000) {
            for (trajet in listOfTrajets) {
                text += trajet.toString() + "\n"
            }
        }
        else {
            var step = 0
            for (trajet in listOfTrajets) {
                if (step < 100000) {
                    text += trajet.toString() + "\n"
                    step+=1
                }
            }
        }
        return text
    }

    fun toJson(id_s: String): String {
        val gson = Gson()

        println("Trajets JSON : ${gson.toJson(this)}")
        val json = AESUtils.encrypt(gson.toJson(this), id_s)
        println("jsonessai" + json)

        return json
    }

    fun saveFromExternalDB(sharedPreferences: SharedPreferences, id_text : String, id_s : String, context: android.content.Context) {
        CustomSettings.getValues(sharedPreferences)
        val database = FirebaseDatabase.getInstance().reference
        // Do network action in this function
        val gson = Gson()

        val dbManager =  TrajetsDB(context)
        println("id_text : "+ id_text)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val json = dataSnapshot.child(id_text).getValue(String::class.java)
                println("Return SaveFromExternalDB : ${json}")
                try {
                    val readed = gson.fromJson(AESUtils.decrypt(json, id_s), Trajets::class.java)
                    println("Trajets coucou " + readed.toPrintTotal(context))
                    for (trajet in readed.listOfTrajets) {
                        println("Trajets add : $trajet")
                        dbManager.Insert(trajet)
                    }
                }
                catch (e: Exception){
                    Toast.makeText(context, "Invalid key", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, databaseError.getMessage()) //Don't ignore errors!
                println("Error : "+databaseError.getMessage())
                Log.d(TAG, "Return SaveFromExternalDB : Error")
            }
        }
        database.addListenerForSingleValueEvent(valueEventListener)

        // Write a message to the database
       /* lateinit var database: DatabaseReference

        database = Firebase.database.getReferenceFromUrl("https://time-to-drive-3c674.firebaseio.com/")

        database.child("StockedJSON").child(randomInt.toString())*/
    }


    fun saveToExternalDB(sharedPreferences: SharedPreferences, context : Context1){
        FirebaseApp.initializeApp(/*context=*/ context)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(SafetyNetAppCheckProviderFactory.getInstance())

        val database = FirebaseDatabase.getInstance()

        CustomSettings.getValues(sharedPreferences)
        if (CustomSettings.id == "-1") {
            val random = Random(System.nanoTime() % 10000000)
            val time : Long  = System.currentTimeMillis()/(1000*3600)
            randomInt = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID) + time.toString(16)
            val myRand = random.nextInt(1000000000).toString(16)
            println("number bytes : " + randomInt.toByteArray().size)
            // Write a message to the database
            val myRef = database.getReference(randomInt)
            val json = toJson(myRand)
            myRef.setValue(json)
                .addOnSuccessListener {
                    println("END :  Success")
                }
                .addOnFailureListener {
                    println("END :  Fail " + it.toString())
                }
            CustomSettings.setID(sharedPreferences, randomInt)
            CustomSettings.setIDS(sharedPreferences, myRand)
        }
        else{
            val myRef = database.getReference(CustomSettings.id)
            println("DBREF" + myRef)
            val json = toJson(CustomSettings.id_s)
            println("JSON : "+ json)
            myRef.setValue(json)
                .addOnSuccessListener {
                    println("END :  Success")
                }
                .addOnFailureListener {
                    println("END :  Fail " + it.toString())
                }
        }
        /**database = Firebase.database.getReference("https://time-to-drive-3c674.firebaseio.com/")**/
    }
}