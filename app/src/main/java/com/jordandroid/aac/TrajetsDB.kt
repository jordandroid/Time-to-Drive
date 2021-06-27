package com.jordandroid.aac

import android.content.ContentValues
import android.content.Context

class TrajetsDB(context: Context) {
    var dbHelper : TrajetsDBManager = TrajetsDBManager(context)


    fun loadTrajets():Trajets{
        val db = dbHelper.readableDatabase
        val trajets = Trajets()
        // Query
        val cursor = db.query(
            DBINFO.dbTable, // The table to query
            null,             // The array of columns to return (pass null to get all)
            null,              // The columns for the WHERE clause
            null,          // The values for the WHERE clause
            null,                   // don't group the rows
            null,                   // don't filter by row groups
            DBINFO.colDate)

        with(cursor) {
            while (moveToNext()) {
                val trajet = Trajet()
                trajet.distance = Integer(getInt(getColumnIndexOrThrow(DBINFO.colDistance)))
                trajet.date = getString(getColumnIndexOrThrow(DBINFO.colDate))
                trajet.trajet = getString(getColumnIndexOrThrow(DBINFO.colTrajetName))
                trajet.ID = (getInt(getColumnIndexOrThrow(DBINFO.colID))).toLong()
                trajets.addATrajet(trajet)
            }
        }
        return trajets
    }

    fun deleteTrajet(trajet: Trajet):Int{
        val db = dbHelper.writableDatabase
        // Define 'where' part of query.
        val selection = "${DBINFO.colID} = ${trajet.ID}"
        // Issue SQL statement.
        return (db.delete(DBINFO.dbTable, selection, null))
    }


    fun Insert(trajet: Trajet){
        val value = ContentValues().apply {
            put(DBINFO.colDate, trajet.date)
            put(DBINFO.colTrajetName, trajet.trajet)
            put(DBINFO.colDistance, trajet.distance.toInt())
        }
        trajet.ID = Insert(value)
    }

    fun Insert(value: ContentValues): Long {
        val db = dbHelper.writableDatabase
        return db!!.insert(DBINFO.dbTable, "", value)
    }
}