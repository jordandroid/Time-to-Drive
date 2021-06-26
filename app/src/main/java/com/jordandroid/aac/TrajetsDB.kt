package com.jordandroid.aac

import android.content.ContentValues
import android.content.Context

class TrajetsDB(context: Context) {
    lateinit var dbHelper : TrajetsDBManager
    init {
        dbHelper = TrajetsDBManager(context)
    }


    fun loadTrajets():Trajets{
        val db = dbHelper.readableDatabase
        var trajets = Trajets()
        // Query
        var cursor = db.query(
            DB_INFO.dbTable, // The table to query
            null,             // The array of columns to return (pass null to get all)
            null,              // The columns for the WHERE clause
            null,          // The values for the WHERE clause
            null,                   // don't group the rows
            null,                   // don't filter by row groups
            DB_INFO.colDate)

        with(cursor) {
            while (moveToNext()) {
                var trajet = Trajet()
                trajet.distance = Integer(getInt(getColumnIndexOrThrow(com.jordandroid.aac.DB_INFO.colDistance)))
                trajet.date = getString(getColumnIndexOrThrow(com.jordandroid.aac.DB_INFO.colDate))
                trajet.trajet = getString(getColumnIndexOrThrow(com.jordandroid.aac.DB_INFO.colTrajetName))
                trajet.ID = (getInt(getColumnIndexOrThrow(com.jordandroid.aac.DB_INFO.colID))).toLong()
                trajets.addATrajet(trajet)
            }
        }
        return trajets
    }

    fun deleteTrajet(trajet: Trajet):Int{
        val db = dbHelper.writableDatabase
        // Define 'where' part of query.
        val selection = "${DB_INFO.colID} = ${trajet.ID}"
        // Issue SQL statement.
        return (db.delete(DB_INFO.dbTable, selection, null))
    }


    fun Insert(trajet: Trajet){
        val value = ContentValues().apply {
            put(DB_INFO.colDate, trajet.date)
            put(DB_INFO.colTrajetName, trajet.trajet)
            put(DB_INFO.colDistance, trajet.distance.toInt())
        }
        trajet.ID = Insert(value)
    }

    fun Insert(value: ContentValues):Long{
        val db = dbHelper.writableDatabase
        val ID= db!!.insert(DB_INFO.dbTable,"",value)
        return ID
    }
}